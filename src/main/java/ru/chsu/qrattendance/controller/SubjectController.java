package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.model.dto.SubjectDto;
import ru.chsu.qrattendance.service.SubjectService;

@Slf4j
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Validated
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping
    public SubjectDto getByTeacher(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return subjectService.getByTeacher(email);
    }

    @GetMapping("/all")
    public SubjectDto getAll(){
        return subjectService.getAll();
    }
}
