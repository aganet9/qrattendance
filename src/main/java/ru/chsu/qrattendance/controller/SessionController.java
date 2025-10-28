package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.model.dto.CreateDto;
import ru.chsu.qrattendance.model.dto.CreateSessionResult;
import ru.chsu.qrattendance.service.SessionService;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateDto dto,
                                                      @AuthenticationPrincipal Jwt jwt) {
        String teacherEmail = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        CreateSessionResult res = sessionService.createSession(dto, teacherEmail, firstName, lastName);
        return ResponseEntity.ok(Map.of("sessionId", res.lectureSession().getId(), "tokens", res.tokens()));
    }
}
