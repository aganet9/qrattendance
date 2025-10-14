package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.model.entity.StudentGroup;
import ru.chsu.qrattendance.model.entity.Teacher;

import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    Optional<StudentGroup> findByName(String name);
}
