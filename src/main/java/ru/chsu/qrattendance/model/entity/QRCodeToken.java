package ru.chsu.qrattendance.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "qr_code_token")
public class QRCodeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "used")
    private boolean used = false;

    @ManyToOne
    private LectureSession session;
}
