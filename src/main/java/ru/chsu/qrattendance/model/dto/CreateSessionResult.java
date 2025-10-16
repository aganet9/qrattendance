package ru.chsu.qrattendance.model.dto;

import ru.chsu.qrattendance.model.entity.LectureSession;

import java.util.List;


public record CreateSessionResult(LectureSession lectureSession, List<String> tokens) {
}
