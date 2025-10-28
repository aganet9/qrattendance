package ru.chsu.qrattendance.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDto {
    private String subject;
    private String room;
    private LocalDate date;
    private List<String> groupNames;
}
