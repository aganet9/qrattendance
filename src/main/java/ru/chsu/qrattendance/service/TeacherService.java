package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.model.dto.TeacherInfo;
import ru.chsu.qrattendance.model.entity.Teacher;
import ru.chsu.qrattendance.repository.TeacherRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Validated
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public TeacherInfo createTeacher(String givenName, String familyName, String email){
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
}
