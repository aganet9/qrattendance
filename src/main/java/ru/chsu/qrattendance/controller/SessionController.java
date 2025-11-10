package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chsu.qrattendance.model.dto.CreateDto;
import ru.chsu.qrattendance.model.dto.CreateSessionResult;
import ru.chsu.qrattendance.service.SessionService;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateDto dto,
                                                      @AuthenticationPrincipal Jwt jwt) {
        log.info("Received session DTO: {}", dto);
        String teacherEmail = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        CreateSessionResult res = sessionService.createSession(dto, teacherEmail, firstName, lastName);
        return ResponseEntity.ok(Map.of("sessionId", res.lectureSession().getId(), "tokens", res.tokens()));
    }

    @GetMapping("/used/{token}")
    public Map<String, Boolean> isUsed(@PathVariable String token) {
        return sessionService.isUsed(token);
    }

    @PostMapping("/terminate/{id}")
    public ResponseEntity<Void> terminate(@PathVariable Long id,
                                          @AuthenticationPrincipal Jwt jwt) {
        String teacherEmail = jwt.getClaimAsString("email");
        sessionService.terminateSession(id, teacherEmail);
        return ResponseEntity.ok().build();
    }
}
