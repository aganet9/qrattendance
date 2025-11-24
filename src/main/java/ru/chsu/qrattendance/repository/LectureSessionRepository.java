package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.chsu.qrattendance.model.entity.LectureSession;

import java.util.List;

public interface LectureSessionRepository extends JpaRepository<LectureSession, Long> {
    @EntityGraph(attributePaths = {"studentGroups", "teacher"})
    @Query("select l from LectureSession l where l.teacher.email = ?1")
    List<LectureSession> findAllByTeacherEmail(String email);
}
