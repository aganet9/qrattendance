package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    List<Student> findByGroup_IdIn(List<Long> groupIds);
}
