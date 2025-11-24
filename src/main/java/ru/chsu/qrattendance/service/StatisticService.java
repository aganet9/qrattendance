package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.chsu.qrattendance.model.dto.GroupStatDto;
import ru.chsu.qrattendance.model.dto.StatisticDto;
import ru.chsu.qrattendance.model.entity.AttendanceRecord;
import ru.chsu.qrattendance.model.entity.LectureSession;
import ru.chsu.qrattendance.model.entity.Student;
import ru.chsu.qrattendance.model.entity.StudentGroup;
import ru.chsu.qrattendance.repository.AttendanceRecordRepository;
import ru.chsu.qrattendance.repository.LectureSessionRepository;
import ru.chsu.qrattendance.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticService {
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LectureSessionRepository lectureSessionRepository;
    private final StudentRepository studentRepository;

    public List<StatisticDto> findAllByTeacher(String email) {
        List<LectureSession> sessions = lectureSessionRepository.findAllByTeacherEmail(email);
        List<StatisticDto> statisticDtos = new ArrayList<>();

        for (LectureSession session : sessions) {
            List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByLectureSessionId(session.getId());

            int attendanceCount = (int) attendanceRecords.stream()
                    .filter(AttendanceRecord::isPresent)
                    .count();

            int totalStudents = session.getStudentGroups().stream()
                    .mapToInt(group -> group.getStudents().size())
                    .sum();

            int missedCount = totalStudents - attendanceCount;

            statisticDtos.add(StatisticDto.builder()
                    .sessionId(session.getId())
                    .date(session.getDate())
                    .room(session.getRoom())
                    .groupNames(session.getStudentGroups().stream()
                            .map(StudentGroup::getName)
                            .toList())
                    .subject(session.getSubject())
                    .attendance(attendanceCount)
                    .missed(missedCount)
                    .build());
        }
        return statisticDtos;
    }

    public GroupStatDto getGroupStat(String groupName, Long sessionId) {
        List<Student> allStudentsInGroup = studentRepository.findAllByGroupName(groupName);

        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByLectureSessionId(sessionId);

        Set<Long> presentStudentIds = attendanceRecords.stream()
                .filter(AttendanceRecord::isPresent)
                .map(record -> record.getStudent().getId())
                .collect(Collectors.toSet());

        List<Student> attendanceStudents = allStudentsInGroup.stream()
                .filter(student -> presentStudentIds.contains(student.getId()))
                .toList();

        List<Student> missedStudents = allStudentsInGroup.stream()
                .filter(student -> !presentStudentIds.contains(student.getId()))
                .toList();

        return GroupStatDto.builder()
                .groupName(groupName)
                .attendanceNames(attendanceStudents.stream()
                        .map(student -> student.getFirstName() + " " + student.getLastName())
                        .toList())
                .missedNames(missedStudents.stream()
                        .map(student -> student.getFirstName() + " " + student.getLastName())
                        .toList())
                .build();
    }
}