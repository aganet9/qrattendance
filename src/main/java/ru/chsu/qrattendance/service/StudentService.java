package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.chsu.qrattendance.model.dto.StudentInfo;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.model.entity.StudentGroup;
import ru.chsu.qrattendance.repository.StudentGroupRepository;
import ru.chsu.qrattendance.repository.StudentRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Validated
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public StudentInfo createStudent(String givenName, String familyName, String email, String group) {
        String cacheKey = "student:" + email;
        StudentInfo cached = (StudentInfo) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }


        Student student = studentRepository.findByEmail(email).orElseGet(() -> {
            Student newStudent = new Student();
            newStudent.setFirstName(givenName);
            newStudent.setLastName(familyName);
            newStudent.setEmail(email);

            StudentGroup studentGroup = studentGroupRepository.findByName(group).orElseGet(() -> {
                StudentGroup newStudentGroup = new StudentGroup();
                newStudentGroup.setName(group);
                return studentGroupRepository.save(newStudentGroup);
            });

            newStudent.setGroup(studentGroup);
            return studentRepository.save(newStudent);
        });

        StudentInfo studentInfo = StudentInfo.builder()
                .givenName(student.getFirstName())
                .familyName(student.getLastName())
                .email(student.getEmail())
                .group(student.getGroup().getName())
                .build();

        redisTemplate.opsForValue().set(cacheKey, studentInfo, Duration.ofHours(24));
        return studentInfo;
    }
}
