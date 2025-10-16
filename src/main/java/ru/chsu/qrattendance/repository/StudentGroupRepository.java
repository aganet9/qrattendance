package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.StudentGroup;

import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    Optional<StudentGroup> findByName(String name);
}
