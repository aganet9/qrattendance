package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chsu.qrattendance.model.entity.StudentGroup;
import ru.chsu.qrattendance.repository.StudentGroupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentGroupService {
    private final StudentGroupRepository studentGroupRepository;

    public List<String> getAllStudentGroupsNames(){
        return studentGroupRepository.findAll().stream()
                .map(StudentGroup::getName)
                .toList();
    }
}
