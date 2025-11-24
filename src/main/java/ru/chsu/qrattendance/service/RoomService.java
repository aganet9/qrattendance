package ru.chsu.qrattendance.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chsu.qrattendance.model.dto.RoomDto;
import ru.chsu.qrattendance.model.entity.Room;
import ru.chsu.qrattendance.repository.RoomRepository;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomDto findAll() {
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomNames(roomRepository.findAll().stream()
                .map(Room::getName)
                .toList());
        return roomDto;
    }
}
