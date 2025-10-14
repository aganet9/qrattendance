package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.QRCodeToken;

import java.util.Optional;

public interface QRCodeTokenRepository extends JpaRepository<QRCodeToken, Long> {
    Optional<QRCodeToken> findByToken(String token);
}
