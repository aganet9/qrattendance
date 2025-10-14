package ru.chsu.qrattendance.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public final class CreateSessionRequest {
    private String subject;
    private String room;
    private LocalDateTime date;
    private List<Long> groupIds;
}
