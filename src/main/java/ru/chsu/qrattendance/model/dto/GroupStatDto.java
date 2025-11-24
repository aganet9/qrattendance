package ru.chsu.qrattendance.model.dto;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupStatDto {
    private String groupName;
    private List<String> attendanceNames;
    private List<String> missedNames;
}
