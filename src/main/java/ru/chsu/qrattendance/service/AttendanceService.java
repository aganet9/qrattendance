package ru.chsu.qrattendance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.model.entity.AttendanceRecord;
import ru.chsu.qrattendance.model.entity.LectureSession;
import ru.chsu.qrattendance.model.entity.QRCodeToken;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.repository.AttendanceRecordRepository;
import ru.chsu.qrattendance.repository.LectureSessionRepository;
import ru.chsu.qrattendance.repository.QRCodeTokenRepository;
import ru.chsu.qrattendance.repository.StudentRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Validated
public class AttendanceService {

    private final QRCodeTokenRepository qrCodeTokenRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LectureSessionRepository lectureSessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long tokenTtlSeconds;

    public AttendanceService(QRCodeTokenRepository qrCodeTokenRepository,
                             StudentRepository studentRepository,
                             AttendanceRecordRepository attendanceRecordRepository,
                             LectureSessionRepository lectureSessionRepository,
                             RedisTemplate<String, Object> redisTemplate,
                             @Value("${app.token.ttl-seconds:900}") long tokenTtlSeconds) {
        this.qrCodeTokenRepository = qrCodeTokenRepository;
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.lectureSessionRepository = lectureSessionRepository;
        this.redisTemplate = redisTemplate;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    @Transactional
    public boolean markAttendance(String token, String studentEmail) {
        // проверка токена, чтобы несколько студентов не отметились одним
        String usedKey = "qr:used:" + token;

        Boolean reserved = redisTemplate.opsForValue().setIfAbsent(usedKey, studentEmail);
        if (!Boolean.TRUE.equals(reserved)) {
            return false;
        }
        redisTemplate.expire(usedKey, Duration.ofSeconds(tokenTtlSeconds));

        // проверка существования токена в redis
        String tokenKey = "qr:token:" + token;
        Object sessionIdObj = redisTemplate.opsForValue().get(tokenKey);
        if (sessionIdObj == null) {
            redisTemplate.delete(usedKey);
            return false;
        }
        Long sessionId = Long.valueOf(sessionIdObj.toString());

        // есть ли студент
        Student student = studentRepository.findByEmail(studentEmail).orElse(null);
        if (student == null) {
            redisTemplate.delete(usedKey);
            return false;
        }

        // есть ли лекция
        LectureSession lectureSession = lectureSessionRepository.findById(sessionId).orElse(null);
        if (lectureSession == null) {
            redisTemplate.delete(usedKey);
            return false;
        }

        // из этих ли групп студент
        boolean belongs = lectureSession.getStudentGroups().stream()
                .anyMatch(g -> g.getId().equals(student.getGroup().getId()));
        if (!belongs) {
            redisTemplate.delete(usedKey);
            return false;
        }

        // запись посещения
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudent(student);
        attendanceRecord.setLectureSession(lectureSession);
        attendanceRecord.setPresent(true);
        attendanceRecord.setTimestamp(LocalDateTime.now());
        attendanceRecordRepository.save(attendanceRecord);

        // пометка токена как использованного
        Optional<QRCodeToken> tokenEntity = qrCodeTokenRepository.findByToken(token);
        tokenEntity.ifPresent(t -> {
            t.setUsed(true);
            qrCodeTokenRepository.save(t);
        });
        return true;
    }
}
