package ru.chsu.qrattendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chsu.qrattendance.model.dto.StudentInfo;
import ru.chsu.qrattendance.model.dto.TeacherInfo;
import ru.chsu.qrattendance.service.StudentService;
import ru.chsu.qrattendance.service.TeacherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final StudentService studentService;
    private final TeacherService teacherService;

    @PostMapping
    public ResponseEntity<Object> syncUser(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");

        List<String> roles = extractRolesFromJwt(jwt);
        boolean isTeacher = roles.contains("TEACHER");
        boolean isStudent = roles.contains("STUDENT");

        if (isTeacher) {
            TeacherInfo teacher = teacherService.createTeacher(givenName, familyName, email);
            return ResponseEntity.ok(teacher);
        } else if (isStudent) {
            String group = jwt.getClaimAsString("group");
            StudentInfo student = studentService.createStudent(givenName, familyName, email, group);
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.badRequest()
                    .body("Available roles: " + roles);
        }
    }

    private List<String> extractRolesFromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?>) {
                return ((List<?>) rolesObj).stream()
                        .map(Object::toString)
                        .toList();
            }
        }
        return List.of();
    }
}
