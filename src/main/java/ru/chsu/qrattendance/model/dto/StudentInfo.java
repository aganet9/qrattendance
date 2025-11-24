package ru.chsu.qrattendance.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
    @NotBlank
    private String givenName;
    @NotBlank
    private String familyName;
    @NotBlank
    private String email;
    @NotBlank
    private String group;
}
