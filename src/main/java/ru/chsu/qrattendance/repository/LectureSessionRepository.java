package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.LectureSession;

public interface LectureSessionRepository extends JpaRepository<LectureSession, Long> {
}
