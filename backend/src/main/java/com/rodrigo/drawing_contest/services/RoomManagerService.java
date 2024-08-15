package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.events.UserInactivityEvent;
import com.rodrigo.drawing_contest.exceptions.*;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RoomManagerService {

    private final UserService userService;
    private final RoomPersistenceService roomPersistenceService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ApplicationEventPublisher eventPublisher;
    private static final int GAME_DURATION = 1;

    @Transactional
    public Room createPrivateRoom(User user, String password) {
        if (this.roomPersistenceService.getRoomIdOfUser(user.getId()) != null)
            throw new UserIsAlreadyInARoomException("cannot create a new room because user {" + user.getId() + "} is already in a room");

        Room room = new Room(null, password, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        Room savedRoom = this.roomPersistenceService.saveRoom(room);
        this.roomPersistenceService.addUserToRoom(user.getId(), savedRoom.getId());

        return savedRoom;
    }

    @Transactional
    public Room enterInPrivateRoom(User user, UUID roomId, String roomPassword) {
        if (this.roomPersistenceService.getRoomIdOfUser(user.getId()) != null)
            throw new UserIsAlreadyInARoomException("cannot create a new room because user {" + user.getId() + "} is already in a room");

        Room room = this.roomPersistenceService.findRoomById(roomId);
        if (room.getAccessType() != RoomAccessTypeEnum.PRIVATE)
            throw new EntityNotFoundException("room with id {" + roomId + "} not found");

        if (room.getStatus() != RoomStatusEnum.WAITING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot enter in room {" + roomId + "} because match already started");

        if (!Objects.equals(room.getPassword(), roomPassword))
            throw new RoomPasswordDontMatchException("room password do not match");

        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        this.roomPersistenceService.addUserToRoom(user.getId(), room.getId());
        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public Room leaveRoom(User user) {
        UUID roomId = this.roomPersistenceService.getRoomIdOfUser(user.getId());
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("cannot leave room because user {" + user.getId() + "} is not in any room");

        Room room = this.roomPersistenceService.findRoomById(roomId);

        room.removeUser(user.getId());

        this.roomPersistenceService.removeUserFromRoom(user.getId());
        if (room.getUsers().isEmpty()) {
            this.roomPersistenceService.deleteRoom(room.getId());
            return null;
        } else {
            return this.roomPersistenceService.saveRoom(room);
        }
    }

    @Transactional
    public Room changeUserStatus(User user) {
        UUID roomId = this.roomPersistenceService.getRoomIdOfUser(user.getId());
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("cannot change user status because user {" + user.getId() + "} is not in any room");

        Room room = this.roomPersistenceService.findRoomById(roomId);
        if (room.getStatus() != RoomStatusEnum.WAITING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot set user to READY because room status are not WAITING");

        room.getUsers().stream()
                .filter(userRedis -> userRedis.getUserId().equals(user.getId()))
                .findFirst()
                .ifPresent((userRedis) -> {
                    if (userRedis.getStatus() == UserRedis.WaitingPlayerStatusEnum.WAITING)
                        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
                    else
                        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.WAITING);
                });

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public Room startGame(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.WAITING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot start game because room status are not WAITING");

        Instant startTime = Instant.now().plus(Duration.ofSeconds(30));
        Instant endTime = startTime.plus(Duration.ofMinutes(GAME_DURATION));
        room.setStatus(RoomStatusEnum.PLAYING);
        room.setStartTime(startTime);
        room.setEndTime(endTime);

        this.scheduler.schedule(() ->
                this.checkDrawings(roomId),
                Duration.between(Instant.now(), endTime.plusSeconds(10)).toMillis(),
                TimeUnit.MILLISECONDS
        );

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    private void checkDrawings(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);
        System.out.println(room.toString());

        if (room.getStatus() != RoomStatusEnum.PLAYING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot check drawings because room status is not PLAYING");

        List<UserRedis> users = room.getUsers();
        for (UserRedis userRedis : users) {
            if (userRedis.getSvg() == null) {
                System.out.println("user {" + userRedis.getUsername() + "} removed by inactivity");
                User user = this.userService.findUserByUsername(userRedis.getUsername());
                this.leaveRoom(user);
                this.eventPublisher.publishEvent(new UserInactivityEvent(this, user.getUsername()));
            }
        }
    }

    @Transactional
    public Room setUserDraw(User user, String drawSvg) {
        UUID roomId = this.roomPersistenceService.getRoomIdOfUser(user.getId());
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("cannot leave room because user {" + user.getId() + "} is not in any room");

        Room room = this.roomPersistenceService.findRoomById(roomId);
        if (room.getStatus() != RoomStatusEnum.PLAYING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot set user draw because room status are not PLAYING");

        room.getUsers().stream()
                .filter(userRedis -> userRedis.getUserId().equals(user.getId()))
                .findFirst()
                .ifPresent(userRedis -> userRedis.setSvg(drawSvg));

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public Room startVoting(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.PLAYING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot start voting because room status are not PLAYING");

        room.setStatus(RoomStatusEnum.VOTING);
        return this.roomPersistenceService.saveRoom(room);
    }
}
