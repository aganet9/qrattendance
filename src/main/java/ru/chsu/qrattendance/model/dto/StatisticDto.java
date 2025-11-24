package ru.chsu.qrattendance.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDto {
    private Long sessionId;
    private LocalDate date;
    private List<String> groupNames;
    private String room;
    private String subject;
    private Integer attendance;
    private Integer missed;
}
