package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.RoomPasswordDontMatchException;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import com.rodrigo.drawing_contest.repositories.RoomRepository;
import com.rodrigo.drawing_contest.repositories.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    public void createRoom(User user, String password) {}

    public void enterInRoom(User user, Long roomId, String roomPassword) {
        Room room = this.roomRepository.findRoom(roomId);

        if (!Objects.equals(room.getPassword(), roomPassword))
            throw new RoomPasswordDontMatchException("room password do not match");

        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        this.userRoomRepository.addUserToRoom(user.getId(), roomId);
        this.roomRepository.updateRoom(roomId, room);
    }

    public void leaveRoom(User user) {
        Long roomId = this.userRoomRepository.getRoomIdOfUser(user.getId());
        Room room = this.roomRepository.findRoom(roomId);
        room.removeUser(user.getId());
        this.roomRepository.updateRoom(roomId, room);
        this.userRoomRepository.removeUserFromRoom(user.getId());
    }
}
