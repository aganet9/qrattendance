package ru.chsu.qrattendance.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateDto {
    private String subject;
    private String room;
    private String date;
    private List<Long> groupIds;
}
