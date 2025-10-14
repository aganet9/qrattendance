package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.model.dto.CreateDto;
import ru.chsu.qrattendance.model.dto.CreateSessionRequest;
import ru.chsu.qrattendance.model.dto.CreateSessionResult;
import ru.chsu.qrattendance.service.SessionService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDto dto,
                                    @AuthenticationPrincipal Jwt jwt) {
        String teacherEmail = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("firstName");
        String lastName = jwt.getClaimAsString("lastName");
        CreateSessionRequest req = new CreateSessionRequest();
        req.setSubject(dto.subject);
        req.setRoom(dto.room);
        if (dto.date != null) {
            req.setDate(LocalDateTime.parse(dto.date));
        }
        req.setGroupIds(dto.groupIds);
        CreateSessionResult res = sessionService.createSession(req, teacherEmail, firstName, lastName);
        return ResponseEntity.ok(Map.of("sessionId", res.lectureSession().getId(), "tokens", res.tokens()));
    }
}
