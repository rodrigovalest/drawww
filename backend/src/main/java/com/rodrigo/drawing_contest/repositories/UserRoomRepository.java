package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.exceptions.UserIsAlreadyInARoomException;
import com.rodrigo.drawing_contest.exceptions.UserIsNotInAnyRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRoomRepository {

    private final RedisTemplate<String, String> redisTemplate;
    public static final String USER_ROOM_KEY_PREFIX = "userRooms:";

    public void addUserToRoom(Long userId, Long roomId) {
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(USER_ROOM_KEY_PREFIX + userId.toString())))
            throw new UserIsAlreadyInARoomException("user {" + userId + "} is already in a room");
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, roomId.toString());
    }

    public Long getRoomIdOfUser(Long userId) {
        String roomId = this.redisTemplate.opsForValue().get(USER_ROOM_KEY_PREFIX + userId);
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("user is not in any room");
        return Long.parseLong(roomId);
    }

    public void removeUserFromRoom(Long userId) {
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(USER_ROOM_KEY_PREFIX + userId)))
            throw new UserIsNotInAnyRoomException("user { " + userId + " } is not in any room");
        this.redisTemplate.delete(USER_ROOM_KEY_PREFIX + userId);
    }

    public boolean userIsInAnyRoom(Long userId) {
        return this.redisTemplate.hasKey(USER_ROOM_KEY_PREFIX + userId);
    }
}
