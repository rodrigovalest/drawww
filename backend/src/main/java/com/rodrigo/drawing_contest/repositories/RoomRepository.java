package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.exceptions.InvalidRoomException;
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

    public Room saveRoom(Room room) {
        this.validateRoom(room);
        if (room.getId() == null) room.setId(UUID.randomUUID());
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);
        return room;
    }

    public Room findRoom(UUID roomId) {
        return this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + roomId.toString());
    }

    public void deleteRoom(UUID roomId) {
        this.redisTemplate.delete(ROOM_KEY_PREFIX + roomId.toString());
    }

    private void validateRoom(Room room) {
        if (room.getAccessType() == null)
            throw new InvalidRoomException("room access type cannot be null");
        if (room.getStatus() == null)
            throw new InvalidRoomException("room status cannot be null");
        if (room.getSize() == null)
            throw new InvalidRoomException("room size cannot not be null");
    }
}
