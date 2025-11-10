package ru.chsu.qrattendance.model.dto;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherInfo {
    private String givenName;
    private String familyName;
    private String email;
}
