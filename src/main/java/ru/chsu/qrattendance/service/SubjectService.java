package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chsu.qrattendance.model.dto.SubjectDto;
import ru.chsu.qrattendance.model.entity.Subject;
import ru.chsu.qrattendance.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectDto getByTeacher(String email) {
        List<Subject> subjects = subjectRepository.findByTeachers_Email(email).orElse(null);
        List<String> subjectNames = subjects != null
                ? subjects.stream()
                .map(Subject::getName)
                .toList()
                : null;
        return getBySubjectNames(subjectNames);
    }

    public SubjectDto getAll() {
        List<String> subjectNames = subjectRepository.findAll().stream()
                .map(Subject::getName)
                .toList();
        return getBySubjectNames(subjectNames);
    }

    public SubjectDto getBySubjectNames(List<String> subjectNames) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setSubjectNames(subjectNames);
        return subjectDto;
    }
}
