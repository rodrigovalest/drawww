package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.events.StartResultEvent;
import com.rodrigo.drawing_contest.events.StartingVotingForNextDrawingEvent;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ThemeService themeService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ApplicationEventPublisher eventPublisher;
    private static final int GAME_DURATION_MINUTES = 1;
    private static final int GAME_DURATION_SECONDS = 30;
    private static final int VOTING_DURATION_SECONDS = 20;

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
        if (!room.getUsers().stream().allMatch(userRedis -> userRedis.getStatus() == UserRedis.WaitingPlayerStatusEnum.READY))
            throw new CannotStartMatchBecauseNotAllUsersAreReadyException("cannot start game because not all users are READY");

        Instant startTime = Instant.now().plus(Duration.ofSeconds(1));
        Instant endTime = startTime.plus(Duration.ofMinutes(GAME_DURATION_MINUTES)).plus(Duration.ofSeconds(GAME_DURATION_SECONDS));
        room.setStatus(RoomStatusEnum.PLAYING);
        room.setStartTimePlaying(startTime);
        room.setEndTimePlaying(endTime);
        room.setTheme(this.themeService.getRandomTheme());

        System.out.println("Starting game at: " + startTime + ". With end at: " + endTime);

        this.scheduler.schedule(() ->
                this.handlePlayingTimeout(roomId),
                Duration.between(startTime, endTime.plus(Duration.ofMinutes(GAME_DURATION_MINUTES))).toMillis(),
                TimeUnit.MILLISECONDS
        );

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public Room setUserDraw(User user, String svgDraw) {
        UUID roomId = this.roomPersistenceService.getRoomIdOfUser(user.getId());
        if (roomId == null)
            throw new UserIsNotInAnyRoomException("cannot set draw because user {" + user.getUsername() + "} is not in any room");

        Room room = this.roomPersistenceService.findRoomById(roomId);
        if (room.getStatus() != RoomStatusEnum.PLAYING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot set user draw because room status are not PLAYING");

        UserRedis userRedis = room.getUsers().stream()
                .filter(u -> u.getUserId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new UserIsNotInThisRoomException("user {" + user.getUsername() + "} is not in this room"));

        userRedis.setSvgDraw(svgDraw);
        room.getUsers().replaceAll(u -> u.getUsername().equals(user.getUsername()) ? userRedis : u);

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public void handlePlayingTimeout(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);
        System.out.println("GAME END at: " + Instant.now() + ". Expected endTime of room: " + room.getEndTimePlaying());

        if (room.getStatus() != RoomStatusEnum.PLAYING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot check drawings because room status is not PLAYING");

        List<UserRedis> users = room.getUsers();
        for (UserRedis userRedis : users) {
            if (userRedis.getSvgDraw() == null) {
                User user = this.userService.findUserByUsername(userRedis.getUsername());
                System.out.println("disconnecting user: " + user.toString());
                room = this.leaveRoom(user);
                this.eventPublisher.publishEvent(new UserInactivityEvent(this, user.getUsername()));
            }
        }

        room.setStatus(RoomStatusEnum.VOTING);
        this.roomPersistenceService.saveRoom(room);
        this.eventPublisher.publishEvent(new StartingVotingForNextDrawingEvent(this, room));
    }

    @Transactional
    public Room startVotingForNextDrawing(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.VOTING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot start voting because room status is not VOTING");

        List<UserRedis> users = room.getUsers();
        if (users.isEmpty())
            throw new CannotStartVotingBecauseRoomIsEmptyException("cannot start VOTING because room is empty");

        Instant startVotingTime = Instant.now().plusSeconds(2);
        Instant endVotingTime = startVotingTime.plus(Duration.ofSeconds(VOTING_DURATION_SECONDS));
        room.setStartTimeVoting(startVotingTime);
        room.setEndTimeVoting(endVotingTime);

        this.scheduler.schedule(
                () -> this.handleVotingTimeout(roomId),
                Duration.ofSeconds(VOTING_DURATION_SECONDS).toMillis(),
                TimeUnit.MILLISECONDS
        );

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public void handleVotingTimeout(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.VOTING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot handle voting timeout because room status is not VOTING");

        room.getUsers().forEach(u -> u.setVotedInCurrentDraw(false));
        room.setCurrentVotingIndex(room.getCurrentVotingIndex() + 1);
        this.roomPersistenceService.saveRoom(room);

        if (room.getCurrentVotingIndex() < room.getUsers().size()) {
            this.eventPublisher.publishEvent(new StartingVotingForNextDrawingEvent(this, room));
        } else {
            this.eventPublisher.publishEvent(new StartResultEvent(this, room));
        }
    }

    @Transactional
    public Room doVote(UUID roomId, String votingUsername, Long rate) {
        if (rate < 1L || rate > 5L)
            throw new InvalidRateException("rate must be an integer between 1 and 5");

        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.VOTING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot vote because room status is not VOTING");

        UserRedis targetUserRedis = room.getUsers().get(room.getCurrentVotingIndex());

        UserRedis votingUser = room.getUsers().stream()
                .filter(user -> Objects.equals(user.getUsername(), votingUsername))
                .findFirst()
                .orElseThrow(() -> new UserIsNotInThisRoomException("user {" + votingUsername + "} is not in this room"));

        if (Objects.equals(targetUserRedis.getUsername(), votingUsername))
            throw new UserCannotVoteForHimselfException("user cannot vote for himself");
        if (votingUser.isVotedInCurrentDraw())
            throw new UserAlreadyVotedException("user {" + votingUsername + "} has already voted in the current draw");

        votingUser.setVotedInCurrentDraw(true);
        targetUserRedis.setVoteCount(targetUserRedis.getVoteCount() + 1);
        targetUserRedis.setVoteSum(targetUserRedis.getVoteSum() + rate);

        room.getUsers().replaceAll(user -> user.getUsername().equals(targetUserRedis.getUsername()) ? targetUserRedis : user);

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public Room startResult(UUID roomId) {
        Room room = this.roomPersistenceService.findRoomById(roomId);

        if (room.getStatus() != RoomStatusEnum.VOTING)
            throw new ActionDoNotMatchWithRoomStatusException("cannot start result PHASE because room status is not VOTING");

        room.setStatus(RoomStatusEnum.RESULT);
        room.getUsers().forEach(u -> {
            Long voteCount = u.getVoteCount();
            Double voteSum = u.getVoteSum();

            if (voteCount != 0) {
                BigDecimal roundedVoteResult = new BigDecimal(voteSum / voteCount).setScale(1, RoundingMode.HALF_UP);
                u.setVoteResult(roundedVoteResult.doubleValue());
            }
        });

        Instant endTime = Instant.now().plus(Duration.ofSeconds(15));
        this.scheduler.schedule(() -> this.gameEnd(roomId), endTime.toEpochMilli(), TimeUnit.MILLISECONDS);

        return this.roomPersistenceService.saveRoom(room);
    }

    @Transactional
    public void gameEnd(UUID roomId) {
        this.roomPersistenceService.deleteRoom(roomId);
    }
}
