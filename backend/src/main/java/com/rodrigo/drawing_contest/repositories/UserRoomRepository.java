package com.rodrigo.drawing_contest.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserRoomRepository {

    private final RedisTemplate<String, String> redisTemplate;
    public static final String USER_ROOM_KEY_PREFIX = "userRooms:";

    public void addUserToRoom(Long userId, UUID roomId) {
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, roomId.toString());
    }

    public UUID getRoomIdOfUser(Long userId) {
        String roomId = this.redisTemplate.opsForValue().get(USER_ROOM_KEY_PREFIX + userId);
        return roomId == null ? null : UUID.fromString(roomId);
    }

    public void removeUserFromRoom(Long userId) {
        this.redisTemplate.delete(USER_ROOM_KEY_PREFIX + userId);
    }
}
