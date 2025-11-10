package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.service.StudentGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudentGroupController {
    private final StudentGroupService studentGroupService;

    @GetMapping
    public List<String> getAllStudentGroupsNames() {
        return studentGroupService.getAllStudentGroupsNames();
    }
}
