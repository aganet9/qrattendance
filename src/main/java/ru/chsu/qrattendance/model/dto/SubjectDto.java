package ru.chsu.qrattendance.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubjectDto {
    @NotNull
    List<String> subjectNames;
}
