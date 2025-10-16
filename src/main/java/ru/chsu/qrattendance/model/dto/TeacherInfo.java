package ru.chsu.qrattendance.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class TeacherInfo {
    private String givenName;
    private String familyName;
    private String email;
}
