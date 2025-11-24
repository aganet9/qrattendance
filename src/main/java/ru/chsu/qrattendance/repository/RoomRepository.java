package ru.chsu.qrattendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.chsu.qrattendance.model.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
