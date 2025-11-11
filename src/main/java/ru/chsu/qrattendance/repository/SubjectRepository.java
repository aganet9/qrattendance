package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
