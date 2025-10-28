package ru.chsu.qrattendance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.model.dto.CreateDto;
import ru.chsu.qrattendance.model.dto.CreateSessionResult;
import ru.chsu.qrattendance.model.entity.LectureSession;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.model.entity.StudentGroup;
import ru.chsu.qrattendance.model.entity.Teacher;
import ru.chsu.qrattendance.repository.LectureSessionRepository;
import ru.chsu.qrattendance.repository.StudentGroupRepository;
import ru.chsu.qrattendance.repository.StudentRepository;
import ru.chsu.qrattendance.repository.TeacherRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
@Validated
public class SessionService {

    private final LectureSessionRepository sessionRepository;
    private final StudentGroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long tokenTtlSeconds;

    public SessionService(LectureSessionRepository sessionRepository,
                          StudentGroupRepository groupRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          RedisTemplate<String, Object> redisTemplate,
                          @Value("${app.token.ttl-seconds:900}") long tokenTtlSeconds) {
        this.sessionRepository = sessionRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.redisTemplate = redisTemplate;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    @Transactional
    public CreateSessionResult createSession(CreateDto createSessionRequest, String teacherEmail,
                                             String firstName,
                                             String lastName) {
        // найти/создать преподавателя
        Teacher teacher = teacherRepository.findByEmail(teacherEmail).orElseGet(() -> {
            Teacher newTeacher = new Teacher();
            newTeacher.setEmail(teacherEmail);
            newTeacher.setFirstName(firstName);
            newTeacher.setLastName(lastName);
            return teacherRepository.save(newTeacher);
        });

        // найти группы студентов
        List<StudentGroup> groups = groupRepository.findAllByNameIn(createSessionRequest.getGroupNames() == null ?
                Collections.emptyList() : createSessionRequest.getGroupNames());

        // создать и сохранить лекцию
        LectureSession lectureSession = new LectureSession();
        lectureSession.setSubject(createSessionRequest.getSubject());
        lectureSession.setRoom(createSessionRequest.getRoom());
        lectureSession.setDate(createSessionRequest.getDate() == null ? LocalDate.now() : createSessionRequest.getDate());
        lectureSession.setTeacher(teacher);
        lectureSession.setStudentGroups(groups);
        lectureSession = sessionRepository.save(lectureSession);

        // все студенты из групп
        Set<Student> uniqueStudents = new HashSet<>();
        if (!groups.isEmpty()) {
            List<Long> gids = groups.stream()
                    .map(StudentGroup::getId)
                    .toList();
            uniqueStudents.addAll(studentRepository.findByGroup_IdIn(gids));
        }
        int count = uniqueStudents.size();

        // уникальный токен по кол-ву студентов
        List<String> tokens = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String token = UUID.randomUUID().toString();
            // сохранение токена в redis
            String key = "qr:token:" + token;
            redisTemplate.opsForValue().set(key, lectureSession.getId(), Duration.ofSeconds(tokenTtlSeconds));
            tokens.add(token);
        }
        return new CreateSessionResult(lectureSession, tokens);
    }
}
