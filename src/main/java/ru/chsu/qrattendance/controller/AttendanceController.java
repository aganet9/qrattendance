package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.model.dto.AttendDto;
import ru.chsu.qrattendance.service.AttendanceService;

@RestController
@RequestMapping("/api/attend")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;


    @PostMapping
    public ResponseEntity<?> attend(@RequestBody AttendDto dto,
                                    @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        boolean ok = attendanceService.markAttendance(dto.token, email);
        return ok ? ResponseEntity.ok("ok") : ResponseEntity.badRequest().body("invalid or used token");
    }
}
