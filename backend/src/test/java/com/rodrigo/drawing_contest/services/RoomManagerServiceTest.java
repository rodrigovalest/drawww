package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.events.StartingVotingForNextDrawingEvent;
import com.rodrigo.drawing_contest.events.UserInactivityEvent;
import com.rodrigo.drawing_contest.exceptions.*;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomManagerServiceTest {

    @Mock
    private RoomPersistenceService roomPersistenceService;

    @Mock
    private ScheduledExecutorService scheduler;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Spy
    @InjectMocks
    private RoomManagerService roomManagerService;


    @Test
    public void createPrivateRoom_WithValidData_ShouldCreateRoom() {
        // Arrange
        String roomPassword = "roompassword";
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        List<UserRedis> userRedisList = new ArrayList<>();
        userRedisList.add(new UserRedis(user.getId(), user.getUsername()));

        Room toSaveRoom = new Room(null, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        toSaveRoom.setUsers(userRedisList);
        Room savedRoom = new Room(UUID.randomUUID(), roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        savedRoom.setUsers(userRedisList);

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        when(this.roomPersistenceService.saveRoom(toSaveRoom)).thenReturn(savedRoom);

        // Act
        Room sut = this.roomManagerService.createPrivateRoom(user, roomPassword);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo(savedRoom);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .saveRoom(eq(toSaveRoom));
        verify(this.roomPersistenceService, times(1))
                .addUserToRoom(eq(user.getId()), eq(savedRoom.getId()));
    }

    @Test
    public void createPrivateRoom_WithUserAlreadyInSomeRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());

        Assertions.assertThatThrownBy(() -> this.roomManagerService.createPrivateRoom(user, "roomPassword"))
                .isInstanceOf(UserIsAlreadyInARoomException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(eq(user.getId()), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithValidData_ShouldEnterInRoom() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(room);

        // Act
        this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        verify(this.roomPersistenceService, times(1))
                .saveRoom(eq(room));
        verify(this.roomPersistenceService, times(1))
                .addUserToRoom(user.getId(), room.getId());
    }

    @Test
    public void enterInPrivateRoom_WithUserIsAlreadyInARoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());

        Assertions.assertThatThrownBy(() -> this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(UserIsAlreadyInARoomException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(0))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithInexistentRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        when(this.roomPersistenceService.findRoomById(roomId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithNonPrivateRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(room);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithInvalidPassword_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "invalidRoomPassword";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        Room room = new Room(roomId, "roompassword", RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(room);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(RoomPasswordDontMatchException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithMatchAlreadyStarted_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.PLAYING, 10L);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(room);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(ActionDoNotMatchWithRoomStatusException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));
        verify(this.roomPersistenceService, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void leaveRoom_WithValidData_ShouldRemoveUserFromRoomAndNotDeleteRoom() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        roomBefore.addUser(new UserRedis(user.getId(), user.getUsername()));
        roomBefore.addUser(new UserRedis(124L, "another_random_user"));
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        Room roomAfter = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        roomAfter.addUser(new UserRedis(124L, "another_random_user"));

        // Act
        Room sut = this.roomManagerService.leaveRoom(user);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(1))
                .removeUserFromRoom(user.getId());
        verify(this.roomPersistenceService, times(0))
                .deleteRoom(any(UUID.class));

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(this.roomPersistenceService, times(1)).saveRoom(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();

        Assertions.assertThat(savedRoom).isEqualTo(roomAfter);
    }

    @Test
    public void leaveRoom_WithValidData_ShouldRemoveUserFromRoomAndDeleteRoomBecauseItsEmpty() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        roomBefore.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        // Act
        Room sut = this.roomManagerService.leaveRoom(user);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(1))
                .removeUserFromRoom(user.getId());
        verify(this.roomPersistenceService, times(1))
                .deleteRoom(eq(roomBefore.getId()));
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any(Room.class));

        Assertions.assertThat(sut).isNull();
    }

    @Test
    public void leaveRoom_WithUserInAnyRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.leaveRoom(user))
                        .isInstanceOf(UserIsNotInAnyRoomException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(0))
                .findRoomById(any());
        verify(this.roomPersistenceService, times(0))
                .removeUserFromRoom(any());
        verify(this.roomPersistenceService, times(0))
                .deleteRoom(any());
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void changeUserStatus_WithValidData_ShouldSetUserStatusToREADY() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        UserRedis userRedis = new UserRedis(user.getId(), user.getUsername());
        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.WAITING);
        roomBefore.addUser(userRedis);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        Room roomAfter = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
        roomAfter.addUser(userRedis);

        // Act
        Room sut = this.roomManagerService.changeUserStatus(user);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(any(UUID.class));

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(this.roomPersistenceService, times(1))
                .saveRoom(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();

        Assertions.assertThat(savedRoom).isEqualTo(roomAfter);
    }

    @Test
    public void changeUserStatus_WithValidData_ShouldSetUserStatusToWAITING() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        UserRedis userRedis = new UserRedis(user.getId(), user.getUsername());
        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
        roomBefore.addUser(userRedis);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        Room roomAfter = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        userRedis.setStatus(UserRedis.WaitingPlayerStatusEnum.WAITING);
        roomAfter.addUser(userRedis);

        // Act
        Room sut = this.roomManagerService.changeUserStatus(user);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(any(UUID.class));

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(this.roomPersistenceService, times(1))
                .saveRoom(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();

        Assertions.assertThat(savedRoom).isEqualTo(roomAfter);
    }

    @Test
    public void changeUserStatus_WithRoomIsNotInWAITINGStatus_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);
        roomBefore.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.changeUserStatus(user))
                .isInstanceOf(ActionDoNotMatchWithRoomStatusException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(any(UUID.class));
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void changeUserStatus_WithUserInAnyRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);

        Assertions.assertThatThrownBy(() -> this.roomManagerService.changeUserStatus(user))
                .isInstanceOf(UserIsNotInAnyRoomException.class);

        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(0))
                .findRoomById(any());
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

//    @Test
//    public void startGame_WithValidData_ShouldSetStatusToPLAYINGAndSchedulePlayingTimeoutHandler() {
//        // Arrange
//        UUID roomId = UUID.randomUUID();
//
//        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
//
//        UserRedis userRedis1 = new UserRedis(10L, "someusername");
//        userRedis1.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
//        roomBefore.addUser(userRedis1);
//
//        UserRedis userRedis2 = new UserRedis(65L, "someusername");
//        userRedis2.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
//        roomBefore.addUser(userRedis2);
//
//        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);
//
//        // Act
//        Room sut = this.roomManagerService.startGame(roomId);
//
//        // Assert
//        verify(this.roomPersistenceService, times(1))
//                .findRoomById(roomId);
//        verify(this.scheduler, times(1))
//                .schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
//
//        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
//        verify(this.roomPersistenceService, times(1))
//                .saveRoom(roomCaptor.capture());
//        Room savedRoom = roomCaptor.getValue();
//
//        Assertions.assertThat(savedRoom).isNotNull();
//        Assertions.assertThat(savedRoom.getStatus()).isEqualTo(RoomStatusEnum.PLAYING);
//        Assertions.assertThat(savedRoom.getStartTimePlaying()).isNotNull();
//        Assertions.assertThat(savedRoom.getEndTimePlaying()).isNotNull();
//    }

    @Test
    public void setUserDraw_WithValidData_ShouldSaveUserDraw() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));


        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);
        Room beforeRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);
        UserRedis userRedis1 = new UserRedis(user.getId(), user.getUsername());
        UserRedis userRedis2 = new UserRedis(15L, "someusername2");
        beforeRoom.addUser(userRedis1);
        beforeRoom.addUser(userRedis2);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(beforeRoom);

        String drawSvg = "fake_draw_in_svg_format";

        Room afterRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);
        userRedis1.setSvg(drawSvg);
        afterRoom.addUser(userRedis1);
        afterRoom.addUser(userRedis2);

        // Act
        Room room = this.roomManagerService.setUserDraw(user, drawSvg);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);

        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(this.roomPersistenceService, times(1))
                .saveRoom(roomCaptor.capture());
        Room savedRoom = roomCaptor.getValue();

        Assertions.assertThat(savedRoom).isEqualTo(afterRoom);
    }

    @Test
    public void setUserDraw_WithUserInNotAnyRoom_ThrowsException() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String drawSvg = "fake_draw_in_svg_format";

        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(null);

        // Act
        Assertions.assertThatThrownBy(() -> this.roomManagerService.setUserDraw(user, drawSvg))
                .isInstanceOf(UserIsNotInAnyRoomException.class);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(0))
                .findRoomById(any());
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void setUserDraw_WithRoomIsNotInPLAYINGStatus_ThrowsException() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        String drawSvg = "fake_draw_in_svg_format";

        UUID roomId = UUID.randomUUID();
        when(this.roomPersistenceService.getRoomIdOfUser(user.getId())).thenReturn(roomId);
        Room beforeRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.RESULT, 10L);
        UserRedis userRedis1 = new UserRedis(user.getId(), user.getUsername());
        UserRedis userRedis2 = new UserRedis(15L, "someusername2");
        beforeRoom.addUser(userRedis1);
        beforeRoom.addUser(userRedis2);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(beforeRoom);

        // Act
        Assertions.assertThatThrownBy(() -> this.roomManagerService.setUserDraw(user, drawSvg))
                .isInstanceOf(ActionDoNotMatchWithRoomStatusException.class);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void startGame_WithNotAllUsersREADY_ThrowsException() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);

        UserRedis userRedis1 = new UserRedis(10L, "someusername");
        userRedis1.setStatus(UserRedis.WaitingPlayerStatusEnum.WAITING);
        roomBefore.addUser(userRedis1);

        UserRedis userRedis2 = new UserRedis(65L, "someusername");
        userRedis2.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
        roomBefore.addUser(userRedis2);

        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        // Act
        Assertions.assertThatThrownBy(() -> this.roomManagerService.startGame(roomId))
                .isInstanceOf(CannotStartMatchBecauseNotAllUsersAreReadyException.class);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .findRoomById(any(UUID.class));
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void startGame_WithRoomIsNotInWAITINGStatus_ThrowsException() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        Room roomBefore = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.VOTING, 10L);

        UserRedis userRedis1 = new UserRedis(10L, "someusername");
        userRedis1.setStatus(UserRedis.WaitingPlayerStatusEnum.WAITING);
        roomBefore.addUser(userRedis1);

        UserRedis userRedis2 = new UserRedis(65L, "someusername");
        userRedis2.setStatus(UserRedis.WaitingPlayerStatusEnum.READY);
        roomBefore.addUser(userRedis2);

        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(roomBefore);

        // Act
        Assertions.assertThatThrownBy(() -> this.roomManagerService.startGame(roomId))
                .isInstanceOf(ActionDoNotMatchWithRoomStatusException.class);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .findRoomById(any(UUID.class));
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
    }

    @Test
    public void handlePlayingTimeout_WithValidData_ShouldSetRoomStatusToVOTINGAndPublishStartVotingEvent() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        Room beforeRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);
        UserRedis userRedis1 = new UserRedis(547L, "someusername");
        userRedis1.setSvg("fake_draw_in_svg_format");
        UserRedis userRedis2 = new UserRedis(15L, "someusername2");
        userRedis2.setSvg("fake_draw_in_svg_format");
        beforeRoom.addUser(userRedis1);
        beforeRoom.addUser(userRedis2);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(beforeRoom);

        Room afterRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.VOTING, 10L);
        afterRoom.addUser(userRedis1);
        afterRoom.addUser(userRedis2);

        // Act
        this.roomManagerService.handlePlayingTimeout(roomId);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(1))
                .saveRoom(eq(afterRoom));
        verify(this.eventPublisher, times(1))
                .publishEvent(any(StartingVotingForNextDrawingEvent.class));
        verify(this.eventPublisher, times(0))
                .publishEvent(any(UserInactivityEvent.class));
    }

    @Test
    public void handlePlayingTimeout_WithRoomIsNotInPLAYINGStatus_ThrowsException() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        Room beforeRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.RESULT, 10L);
        UserRedis userRedis1 = new UserRedis(547L, "someusername");
        userRedis1.setSvg("fake_draw_in_svg_format");
        UserRedis userRedis2 = new UserRedis(15L, "someusername2");
        userRedis2.setSvg("fake_draw_in_svg_format");
        beforeRoom.addUser(userRedis1);
        beforeRoom.addUser(userRedis2);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(beforeRoom);

        // Act
        Assertions.assertThatThrownBy(() -> this.roomManagerService.handlePlayingTimeout(roomId))
                        .isInstanceOf(ActionDoNotMatchWithRoomStatusException.class);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(0))
                .saveRoom(any());
        verify(this.eventPublisher, times(0))
                .publishEvent(any(StartingVotingForNextDrawingEvent.class));
        verify(this.eventPublisher, times(0))
                .publishEvent(any(UserInactivityEvent.class));
    }

    @Test
    public void handlePlayingTimeout_WithValidData_ShouldRemoveUsersThatDidNotSendTheirDrawsAndPublishInactivityEvent() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        User user1 = new User(547L, "someusername", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        User user2 = new User(15L, "someusername2", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room beforeRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);
        UserRedis userRedis1 = new UserRedis(user1.getId(), user1.getUsername());
        UserRedis userRedis2 = new UserRedis(user2.getId(), user2.getUsername());
        userRedis2.setSvg("some_fake_draw_in_svg_format");
        beforeRoom.addUser(userRedis1);
        beforeRoom.addUser(userRedis2);

        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(beforeRoom);
        when(this.userService.findUserByUsername(userRedis1.getUsername())).thenReturn(user1);
        when(this.userService.findUserByUsername(userRedis2.getUsername())).thenReturn(user2);

        Room afterRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.VOTING, 10L);
        afterRoom.addUser(userRedis2);
        doReturn(afterRoom).when(roomManagerService).leaveRoom(user1);

        // Act
        this.roomManagerService.handlePlayingTimeout(roomId);

        // Assert
        verify(this.roomPersistenceService, times(1))
                .findRoomById(roomId);
        verify(this.roomPersistenceService, times(1))
                .saveRoom(eq(afterRoom));
        verify(this.eventPublisher, times(1))
                .publishEvent(any(StartingVotingForNextDrawingEvent.class));
        verify(this.eventPublisher, times(1))
                .publishEvent(any(UserInactivityEvent.class));
    }

    @Test
    public void doVote_WithValidData_ShouldUpdateVotingUserAndUpdateTargetUserVotes() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        String votingUsername = "votingUsername";
        String targetUsername = "targetUsername";
        Long rate = 5L;

        UserRedis userRedis1 = new UserRedis(7L, targetUsername);
        userRedis1.setSvg("somesvg");
        UserRedis userRedis2 = new UserRedis(923L, votingUsername);
        userRedis2.setSvg("somesvg");

        List<UserRedis> persistedUsers = new ArrayList<UserRedis>();
        persistedUsers.add(userRedis1);
        persistedUsers.add(userRedis2);
        Room persistedRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.VOTING, 10L);
        persistedRoom.setTheme("pizza");
        persistedRoom.setStartTimePlaying(Instant.now());
        persistedRoom.setEndTimePlaying(Instant.now().plusSeconds(10000L));
        persistedRoom.setCurrentVotingIndex(0);
        persistedRoom.setUsers(persistedUsers);
        when(this.roomPersistenceService.findRoomById(roomId)).thenReturn(persistedRoom);

        // Act
        Room room = this.roomManagerService.doVote(roomId, votingUsername, targetUsername, rate);

        // Assert
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        verify(this.roomPersistenceService, times(1)).saveRoom(roomCaptor.capture());

        Room savedRoom = roomCaptor.getValue();
        Assertions.assertThat(savedRoom.getUsers().get(0).getVoteCount()).isEqualTo(1L);
        Assertions.assertThat(savedRoom.getUsers().get(0).getVoteSum()).isEqualTo(5.0);
        Assertions.assertThat(savedRoom.getUsers().get(0).isVotedInCurrentDraw()).isEqualTo(false);
        Assertions.assertThat(savedRoom.getUsers().get(1).isVotedInCurrentDraw()).isEqualTo(true);
    }

    @Test
    public void doVote_WithInvalidRate_ThrowsInvalidRateException() {

    }

    @Test
    public void doVote_WithInvalidVotingUser_ThrowsUserCannotVoteForHimselfException() {

    }

    @Test
    public void doVote_WithInvalidTargetUser_ThrowsUserNotUpForVoteException() {

    }

    @Test
    public void doVote_WithInvalidVotingUser_ThrowsUserAlreadyVotedException() {

    }
}
