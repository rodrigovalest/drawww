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

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    public Room createPublicRoom(User user) {
        if (this.userRoomRepository.getRoomIdOfUser(user.getId()) != null)
            throw new UserIsAlreadyInARoomException("cannot create a new room because user {" + user.getId() + "} is already in a room");

        Room room = new Room(null, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        Room savedRoom = this.roomRepository.saveRoom(room);
        this.userRoomRepository.addUserToRoom(user.getId(), savedRoom.getId());

        return savedRoom;
    }

    public Room createPrivateRoom(User user, String password) {
        if (this.userRoomRepository.getRoomIdOfUser(user.getId()) != null)
            throw new UserIsAlreadyInARoomException("cannot create a new room because user {" + user.getId() + "} is already in a room");

        Room room = new Room(null, password, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        Room savedRoom = this.roomRepository.saveRoom(room);
        this.userRoomRepository.addUserToRoom(user.getId(), savedRoom.getId());

        return savedRoom;
    }

    public void enterInPrivateRoom(User user, UUID roomId, String roomPassword) {
        if (this.userRoomRepository.getRoomIdOfUser(user.getId()) != null)
            throw new UserIsAlreadyInARoomException("cannot create a new room because user {" + user.getId() + "} is already in a room");

        Room room = this.roomRepository.findRoom(roomId);
        if (room == null || room.getAccessType() != RoomAccessTypeEnum.PRIVATE)
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");

        if (!Objects.equals(room.getPassword(), roomPassword))
            throw new RoomPasswordDontMatchException("room password do not match");

        if (room.getStatus() != RoomStatusEnum.WAITING)
            throw new RoomNotAvailable("cannot enter in room {" + roomId + "} because match already started");

        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        this.roomRepository.saveRoom(room);
        this.userRoomRepository.addUserToRoom(user.getId(), room.getId());
    }

    public void enterInPublicRoom(User user, UUID roomId) {}

    public void leaveRoom(User user) {
        UUID roomId = this.userRoomRepository.getRoomIdOfUser(user.getId());
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("cannot leave room because user {" + user.getId() + "} is not in any room");

        Room room = this.roomRepository.findRoom(roomId);
        if (room == null)
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");

        room.removeUser(user.getId());
        this.roomRepository.saveRoom(room);
        this.userRoomRepository.removeUserFromRoom(user.getId());
    }

    public void deleteRoom(UUID roomId) {
        Room room = this.roomRepository.findRoom(roomId);
        if (room == null)
            throw new RoomNotFoundException("room with id {" + roomId + "} not found");

        for (UserRedis user : room.getUsers())
            this.userRoomRepository.removeUserFromRoom(user.getId());

        this.roomRepository.deleteRoom(roomId);
    }
}
