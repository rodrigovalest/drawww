package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.exceptions.InvalidRoomException;
import com.rodrigo.drawing_contest.exceptions.RoomAlreadyExistsException;
import com.rodrigo.drawing_contest.exceptions.RoomNotFoundException;
import com.rodrigo.drawing_contest.models.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class RoomRepository {

    private final RedisTemplate<String, Room> redisTemplate;
    public static final String ROOM_KEY_PREFIX = "rooms:";

    public void createRoom(Room room) {
        this.validateRoom(room);
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(ROOM_KEY_PREFIX + room.getId().toString())))
            throw new RoomAlreadyExistsException("room with id " + room.getId() + " already exists");
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);
    }

    public Room findRoom(UUID roomId) {
        Room room = this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + roomId.toString());
        if (room == null)
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");
        return room;
    }

    public void updateRoom(UUID roomId, Room room) {
        this.validateRoom(room);
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(ROOM_KEY_PREFIX + roomId.toString())))
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");
        room.setId(roomId);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + roomId, room);
    }

    public void deleteRoom(UUID roomId) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(ROOM_KEY_PREFIX + roomId.toString())))
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");
        this.redisTemplate.delete(ROOM_KEY_PREFIX + roomId.toString());
    }

    private void validateRoom(Room room) {
        if (room.getId() == null)
            throw new InvalidRoomException("room ID cannot be null");
        if (room.getAccessType() == null)
            throw new InvalidRoomException("room access type cannot be null");
        if (room.getStatus() == null)
            throw new InvalidRoomException("room status cannot be null");
        if (room.getSize() == null || room.getSize() <= 1 || room.getSize() > 10)
            throw new InvalidRoomException("room size should be between 2 and 10");
    }
}
