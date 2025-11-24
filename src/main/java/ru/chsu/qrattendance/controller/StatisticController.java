package ru.chsu.qrattendance.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chsu.qrattendance.model.dto.GroupStatDto;
import ru.chsu.qrattendance.model.dto.StatisticDto;
import ru.chsu.qrattendance.service.StatisticService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping
    public List<StatisticDto> findAllByTeacher(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return statisticService.findAllByTeacher(email);
    }

    @GetMapping("/group")
    public GroupStatDto getGroupStat(@RequestParam @NotBlank String groupName,
                                     @RequestParam @NotNull Long sessionId) {
        return statisticService.getGroupStat(groupName, sessionId);
    }
}
