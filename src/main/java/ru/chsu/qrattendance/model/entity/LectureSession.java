package ru.chsu.qrattendance.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "lecture_session")
public class LectureSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "room")
    private String room;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    private Teacher teacher;

    @ManyToMany
    @JoinTable(name = "lecture_session_groups",
            joinColumns = @JoinColumn(name = "lecture_session_id"),
            inverseJoinColumns = @JoinColumn(name = "student_group_id"))
    private List<StudentGroup> studentGroups;
}
