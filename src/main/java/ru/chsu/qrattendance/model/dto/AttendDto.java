package ru.chsu.qrattendance.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttendDto {
    @NotBlank
    private String token;
}
