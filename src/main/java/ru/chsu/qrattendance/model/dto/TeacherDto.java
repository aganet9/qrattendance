package ru.chsu.qrattendance.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto {
    @NotBlank
    private String givenName;
    @NotBlank
    private String familyName;
    @NotBlank
    private String email;
    @NotNull
    private List<String> subjects;
}
