package ru.chsu.qrattendance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.exception.TeacherNotFoundException;
import ru.chsu.qrattendance.model.dto.SubjectDto;
import ru.chsu.qrattendance.model.dto.TeacherDto;
import ru.chsu.qrattendance.model.dto.TeacherInfo;
import ru.chsu.qrattendance.model.entity.Subject;
import ru.chsu.qrattendance.model.entity.Teacher;
import ru.chsu.qrattendance.repository.SubjectRepository;
import ru.chsu.qrattendance.repository.TeacherRepository;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public TeacherInfo createTeacher(String givenName, String familyName, String email) {
        String cacheKey = "teacher:" + email;
        TeacherInfo cached = (TeacherInfo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Teacher teacher = teacherRepository.findByEmail(email).orElseGet(() -> {
            Teacher newTeacher = new Teacher();
            newTeacher.setEmail(email);
            newTeacher.setFirstName(givenName);
            newTeacher.setLastName(familyName);
            return teacherRepository.save(newTeacher);
        });

        TeacherInfo teacherInfo = TeacherInfo.builder()
                .givenName(teacher.getFirstName())
                .familyName(teacher.getLastName())
                .email(teacher.getEmail())
                .build();

        redisTemplate.opsForValue().set(cacheKey, teacherInfo, Duration.ofHours(24));
        return teacherInfo;
    }

    public TeacherDto getTeacherInfo(String email) {
        Teacher teacher = teacherRepository.findByEmail(email).orElseThrow(() -> new TeacherNotFoundException(email));
        List<Subject> subjects = subjectRepository.findByTeachers_Email(email).orElse(null);
        return TeacherDto.builder()
                .givenName(teacher.getFirstName())
                .familyName(teacher.getLastName())
                .email(teacher.getEmail())
                .subjects(subjects != null
                        ? subjects.stream()
                        .map(Subject::getName)
                        .toList()
                        : null)
                .build();
    }

    @Transactional
    public void updateTeacherSubject(SubjectDto dto, String email) {
        Teacher teacher = teacherRepository.findByEmail(email).orElseThrow(() -> new TeacherNotFoundException(email));
        teacher.getSubjects().clear();

        if (dto.getSubjectNames() != null) {
            List<Subject> subjects = subjectRepository.findByNameIn(dto.getSubjectNames());
            teacher.setSubjects(subjects);
        }

        teacherRepository.save(teacher);
    }
}
