package ru.chsu.qrattendance.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public final class CreateSessionRequest {
    private String subject;
    private String room;
    private LocalDateTime date;
    private List<Long> groupIds;
}
