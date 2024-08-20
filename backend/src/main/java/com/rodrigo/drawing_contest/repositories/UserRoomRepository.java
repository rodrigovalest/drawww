package com.rodrigo.drawing_contest.repositories;

import java.util.UUID;

public interface UserRoomRepository {
    void addUserToRoom(Long userId, UUID roomId);
    UUID getRoomIdOfUser(Long userId);
    void removeUserFromRoom(Long userId);
}
