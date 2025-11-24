package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.chsu.qrattendance.model.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("select s from Student s where s.email = ?1")
    Optional<Student> findByEmail(String email);

    @Query("select s from Student s where s.group.id in ?1")
    List<Student> findByGroup_IdIn(List<Long> groupIds);

    @Query("SELECT s FROM Student s JOIN FETCH s.group WHERE s.group.name = ?1")
    List<Student> findAllByGroupName(String groupName);
}
