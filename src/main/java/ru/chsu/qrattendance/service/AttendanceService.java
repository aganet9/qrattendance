package ru.chsu.qrattendance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.exception.AttendanceException;
import ru.chsu.qrattendance.model.entity.AttendanceRecord;
import ru.chsu.qrattendance.model.entity.LectureSession;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.repository.AttendanceRecordRepository;
import ru.chsu.qrattendance.repository.LectureSessionRepository;
import ru.chsu.qrattendance.repository.StudentRepository;

import java.time.Duration;
import java.time.LocalDate;

@Service
@Validated
public class AttendanceService {

    private final StudentRepository studentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LectureSessionRepository lectureSessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long tokenTtlSeconds;

    public AttendanceService(StudentRepository studentRepository,
                             AttendanceRecordRepository attendanceRecordRepository,
                             LectureSessionRepository lectureSessionRepository,
                             RedisTemplate<String, Object> redisTemplate,
                             @Value("${app.token.ttl-seconds:900}") long tokenTtlSeconds) {
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.lectureSessionRepository = lectureSessionRepository;
        this.redisTemplate = redisTemplate;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    @Transactional
    public boolean markAttendance(String token, String studentEmail) {
        // Резервируем токен на время обработки, чтобы параллельно им не воспользовались
        String processingKey = "qr:processing:" + token;

        Boolean reserved = redisTemplate.opsForValue().setIfAbsent(processingKey, studentEmail);
        if (!Boolean.TRUE.equals(reserved)) {
            throw new AttendanceException("QR-код уже был использован");
        }
        redisTemplate.expire(processingKey, Duration.ofSeconds(tokenTtlSeconds));

        try {
            // проверка существования токена в redis
            String tokenKey = "qr:token:" + token;
            Object sessionIdObj = redisTemplate.opsForValue().get(tokenKey);
            if (sessionIdObj == null) {
                throw new AttendanceException("Некорректный QR-код");
            }
            Long sessionId = Long.valueOf(sessionIdObj.toString());

            // есть ли студент
            Student student = studentRepository.findByEmail(studentEmail).orElse(null);
            if (student == null) {
                throw new AttendanceException("Вас нет в списке студентов");
            }

            // есть ли лекция
            LectureSession lectureSession = lectureSessionRepository.findById(sessionId).orElse(null);
            if (lectureSession == null) {
                throw new AttendanceException("Некорректное создание пары");
            }

            // из этих ли групп студент
            boolean belongs = lectureSession.getStudentGroups().stream()
                    .anyMatch(g -> g.getId().equals(student.getGroup().getId()));
            if (!belongs) {
                throw new AttendanceException("Вы не из этой группы");
            }

            // защита от дублей на уровне приложения + на всякий случай ловим уникальность БД
            if (attendanceRecordRepository.existsByStudent_IdAndLectureSession_Id(student.getId(), lectureSession.getId())) {
                throw new AttendanceException("Вы уже отмечались");
            }

            // запись посещения
            AttendanceRecord attendanceRecord = new AttendanceRecord();
            attendanceRecord.setStudent(student);
            attendanceRecord.setLectureSession(lectureSession);
            attendanceRecord.setPresent(true);
            attendanceRecord.setTimestamp(LocalDate.now());
            try {
                attendanceRecordRepository.save(attendanceRecord);
            } catch (DataIntegrityViolationException ex) {
                throw new AttendanceException("Вы уже отмечались");
            }

            // Помечаем QR как действительно использованный
            String usedKey = "qr:used:" + token;
            redisTemplate.opsForValue().set(usedKey, studentEmail, Duration.ofSeconds(tokenTtlSeconds));

            return true;
        } finally {
            // В любом случае освобождаем резерв
            redisTemplate.delete(processingKey);
        }
    }
}
