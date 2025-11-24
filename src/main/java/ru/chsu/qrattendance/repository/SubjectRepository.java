package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.chsu.qrattendance.model.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("select s from Subject s inner join s.teachers teachers where teachers.email = ?1")
    Optional<List<Subject>> findByTeachers_Email(String email);

    @Query("select s from Subject s where s.name in ?1")
    List<Subject> findByNameIn(List<String> names);
}
