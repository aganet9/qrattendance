package ru.chsu.qrattendance.model.dto;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
    private String givenName;
    private String familyName;
    private String email;
    private String group;
}
