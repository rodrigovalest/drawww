package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.*;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import com.rodrigo.drawing_contest.repositories.RoomRepository;
import com.rodrigo.drawing_contest.repositories.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomPersistenceService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional(readOnly = true)
    public Room findRoomById(UUID id) {
        return this.roomRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("room with id {" + id + "} not found")
        );
    }

    @Transactional
    public Room saveRoom(Room room) {
        return this.roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(UUID roomId) {
        Room room = this.findRoomById(roomId);

        for (UserRedis user : room.getUsers())
            this.userRoomRepository.removeUserFromRoom(user.getUserId());

        this.roomRepository.deleteById(roomId);
    }

    @Transactional
    public void addUserToRoom(Long userId, UUID roomId) {
        this.userRoomRepository.addUserToRoom(userId, roomId);
    }

    @Transactional
    public void removeUserFromRoom(Long userId) {
        this.userRoomRepository.removeUserFromRoom(userId);
    }

    @Transactional(readOnly = true)
    public UUID getRoomIdOfUser(Long userId) {
        return this.userRoomRepository.getRoomIdOfUser(userId);
    }
}
