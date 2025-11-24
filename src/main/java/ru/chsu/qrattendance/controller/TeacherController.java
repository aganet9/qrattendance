package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chsu.qrattendance.model.dto.SubjectDto;
import ru.chsu.qrattendance.model.dto.TeacherDto;
import ru.chsu.qrattendance.service.TeacherService;

@Slf4j
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Validated
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping
    public TeacherDto getTeacherInfo(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return teacherService.getTeacherInfo(email);
    }

    @PatchMapping
    public ResponseEntity<SubjectDto> updateTeacherSubject(@RequestBody SubjectDto dto,
                                                           @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        teacherService.updateTeacherSubject(dto, email);
        return ResponseEntity.ok().body(dto);
    }
}
