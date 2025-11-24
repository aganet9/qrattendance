package ru.chsu.qrattendance.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDto {
    @NotBlank
    private String subject;
    @NotBlank
    private String room;
    @NotNull
    private LocalDate date;
    @NotNull
    private List<String> groupNames;
}
