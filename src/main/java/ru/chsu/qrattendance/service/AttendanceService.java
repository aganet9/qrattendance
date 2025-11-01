package ru.chsu.qrattendance.service;

import org.springframework.beans.factory.annotation.Value;
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
        // проверка токена, чтобы несколько студентов не отметились одним
        //test
        String usedKey = "qr:used:" + token;

        Boolean reserved = redisTemplate.opsForValue().setIfAbsent(usedKey, studentEmail);
        if (!Boolean.TRUE.equals(reserved)) {
            throw new AttendanceException("QR-код уже был использован");
        }
        redisTemplate.expire(usedKey, Duration.ofSeconds(tokenTtlSeconds));

        // проверка существования токена в redis
        String tokenKey = "qr:token:" + token;
        Object sessionIdObj = redisTemplate.opsForValue().get(tokenKey);
        if (sessionIdObj == null) {
            redisTemplate.delete(usedKey);
            throw new AttendanceException("Некорректный QR-код");
        }
        Long sessionId = Long.valueOf(sessionIdObj.toString());

        // проверка не отметился ли студент еще раз
        String studentAttendanceKey = "attendance:session:" + sessionId + ":student:" + studentEmail;
        Boolean alreadyMarked = redisTemplate.opsForValue()
                .setIfAbsent(studentAttendanceKey, "true", Duration.ofMinutes(3));
        if (!Boolean.TRUE.equals(alreadyMarked)) {
            redisTemplate.delete(usedKey);
            throw new AttendanceException("Вы уже отмечались");
        }

        // есть ли студент
        Student student = studentRepository.findByEmail(studentEmail).orElse(null);
        if (student == null) {
            redisTemplate.delete(usedKey);
            redisTemplate.delete(studentAttendanceKey);
            throw new AttendanceException("Вас нет в списке студентов");
        }

        // есть ли лекция
        LectureSession lectureSession = lectureSessionRepository.findById(sessionId).orElse(null);
        if (lectureSession == null) {
            redisTemplate.delete(usedKey);
            throw new AttendanceException("Некорректное создание пары");
        }

        // из этих ли групп студент
        boolean belongs = lectureSession.getStudentGroups().stream()
                .anyMatch(g -> g.getId().equals(student.getGroup().getId()));
        if (!belongs) {
            redisTemplate.delete(usedKey);
            throw new AttendanceException("Вы не из этой группы");
        }

        // запись посещения
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudent(student);
        attendanceRecord.setLectureSession(lectureSession);
        attendanceRecord.setPresent(true);
        attendanceRecord.setTimestamp(LocalDate.now());
        attendanceRecordRepository.save(attendanceRecord);

        return true;
    }
}
