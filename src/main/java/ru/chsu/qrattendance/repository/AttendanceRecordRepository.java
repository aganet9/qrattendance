package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.chsu.qrattendance.model.entity.AttendanceRecord;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    @Query("SELECT ar FROM AttendanceRecord ar JOIN FETCH ar.student WHERE ar.lectureSession.id = ?1")
    List<AttendanceRecord> findByLectureSessionId(Long sessionId);
}
