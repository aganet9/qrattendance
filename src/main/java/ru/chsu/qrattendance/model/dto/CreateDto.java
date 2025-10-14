package ru.chsu.qrattendance.model.dto;

import java.util.List;

public class CreateDto {
    public String subject;
    public String room;
    public String date;
    public List<Long> groupIds;
}
