package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.AttendanceRecord;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByLectureSessionId(Long sessionId);
}
