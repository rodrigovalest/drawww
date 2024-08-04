package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.*;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import com.rodrigo.drawing_contest.repositories.RoomRepository;
import com.rodrigo.drawing_contest.repositories.UserRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @InjectMocks
    private RoomService roomService;

    @Spy
    @InjectMocks
    private RoomService roomServiceSpy;

    @Test
    public void findRoomById_WithValidData_ReturnsRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room room = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        Room sut = this.roomService.findRoomById(roomId);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getId()).isNotNull();
        Assertions.assertThat(sut.getPassword()).isNull();
        Assertions.assertThat(sut.getUsers().size()).isEqualTo(1);
        Assertions.assertThat(sut.getAccessType()).isEqualTo(RoomAccessTypeEnum.PUBLIC);
        Assertions.assertThat(sut.getStatus()).isEqualTo(RoomStatusEnum.WAITING);
        Assertions.assertThat(sut.getSize()).isEqualTo(10L);

        verify(this.roomRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    public void findRoomById_WithInexistentRoomId_ReturnsRoom() {
        UUID roomId = UUID.randomUUID();
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room room = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.roomRepository.findById(roomId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> this.roomService.findRoomById(roomId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    public void createPublicRoom_WithValidData_ShouldCreateRoom() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room room = new Room(UUID.randomUUID(), null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
        when(this.roomRepository.save(any(Room.class))).thenReturn(room);

        // Act
        Room sut = this.roomService.createPublicRoom(user);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getId()).isNotNull();
        Assertions.assertThat(sut.getPassword()).isNull();
        Assertions.assertThat(sut.getUsers().size()).isEqualTo(1);
        Assertions.assertThat(sut.getAccessType()).isEqualTo(RoomAccessTypeEnum.PUBLIC);
        Assertions.assertThat(sut.getStatus()).isEqualTo(RoomStatusEnum.WAITING);
        Assertions.assertThat(sut.getSize()).isEqualTo(10L);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomRepository, times(1))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(1))
                .addUserToRoom(eq(user.getId()), any(UUID.class));
    }

    @Test
    public void createPublicRoom_WithUserAlreadyInSomeRoom_ThrowException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());

        Assertions.assertThatThrownBy(() -> this.roomService.createPublicRoom(user))
                .isInstanceOf(UserIsAlreadyInARoomException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(eq(user.getId()), any(UUID.class));
    }

    @Test
    public void createPrivateRoom_WithValidData_ShouldCreateRoom() {
        // Arrange
        String roomPassword = "roompassword";
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room room = new Room(UUID.randomUUID(), roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
        when(this.roomRepository.save(any(Room.class))).thenReturn(room);

        // Act
        Room sut = this.roomService.createPrivateRoom(user, roomPassword);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getId()).isNotNull();
        Assertions.assertThat(sut.getPassword()).isEqualTo(roomPassword);
        Assertions.assertThat(sut.getUsers().size()).isEqualTo(1);
        Assertions.assertThat(sut.getAccessType()).isEqualTo(RoomAccessTypeEnum.PRIVATE);
        Assertions.assertThat(sut.getStatus()).isEqualTo(RoomStatusEnum.WAITING);
        Assertions.assertThat(sut.getSize()).isEqualTo(10L);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomRepository, times(1))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(1))
                .addUserToRoom(eq(user.getId()), any(UUID.class));
    }

    @Test
    public void createPrivateRoom_WithUserAlreadyInSomeRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());

        Assertions.assertThatThrownBy(() -> this.roomService.createPrivateRoom(user, "roomPassword"))
                .isInstanceOf(UserIsAlreadyInARoomException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(eq(user.getId()), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithValidData_ShouldEnterInRoom() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        // Act
        this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword);

        // Assert
        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1))
                .findRoomById(roomId);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        verify(this.roomRepository, times(1))
                .save(eq(room));
        verify(this.userRoomRepository, times(1))
                .addUserToRoom(user.getId(), room.getId());
    }

    @Test
    public void enterInPrivateRoom_WithUserIsAlreadyInARoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());
        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(UserIsAlreadyInARoomException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(0))
                .findRoomById(roomId);
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithInexistentRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);

        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();
        doThrow(EntityNotFoundException.class).when(roomServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1))
                .findRoomById(roomId);
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithNonPrivateRoom_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);

        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1))
                .findRoomById(roomId);
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithInvalidPassword_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);

        String roomPassword = "invalidRoomPassword";
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, "roompassword", RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(RoomPasswordDontMatchException.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1))
                .findRoomById(roomId);
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void enterInPrivateRoom_WithMatchAlreadyStarted_ThrowsException() {
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);

        String roomPassword = "123mudar";
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.PLAYING, 10L);
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
                .isInstanceOf(RoomNotAvailable.class);

        verify(this.userRoomRepository, times(1))
                .getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1))
                .findRoomById(roomId);
        verify(this.roomRepository, times(0))
                .save(any(Room.class));
        verify(this.userRoomRepository, times(0))
                .addUserToRoom(anyLong(), any(UUID.class));
    }

    @Test
    public void leaveRoom_WithValidData_ShouldRemoveUserFromRoom() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(roomId);
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        // Act
        this.roomServiceSpy.leaveRoom(user);

        // Assert
        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1)).findRoomById(roomId);
        room.removeUser(user.getId());
        verify(this.roomRepository, times(1)).save(eq(room));
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user.getId());
    }

    @Test
    public void leaveRoom_WithUserNotInAnyRoom_ThrowsException() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.leaveRoom(user))
                .isInstanceOf(UserIsNotInAnyRoomException.class);

        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(0)).findRoomById(any(UUID.class));
        verify(this.roomRepository, times(0)).save(any(Room.class));
        verify(this.userRoomRepository, times(0)).removeUserFromRoom(user.getId());
    }

    @Test
    public void leaveRoom_WithInexistentRoom_ThrowsException() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        UUID roomId = UUID.randomUUID();
        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(roomId);
        doThrow(EntityNotFoundException.class).when(roomServiceSpy).findRoomById(roomId);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.leaveRoom(user))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
        verify(this.roomServiceSpy, times(1)).findRoomById(roomId);
        verify(this.roomRepository, times(0)).save(any(Room.class));
        verify(this.userRoomRepository, times(0)).removeUserFromRoom(user.getId());
    }

    @Test
    public void deleteRoom_WithValidRoomId_ShouldDeleteRoomAndRemoveUsers() {
        // Arrange
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        User user1 = new User(11L, "rolmundo", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        User user2 = new User(12L, "tonio", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        User user3 = new User(13L, "fagner", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, null, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        room.addUser(new UserRedis(user1.getId(), user1.getUsername()));
        room.addUser(new UserRedis(user2.getId(), user2.getUsername()));
        room.addUser(new UserRedis(user3.getId(), user3.getUsername()));
        doReturn(room).when(roomServiceSpy).findRoomById(roomId);

        // Act
        this.roomServiceSpy.deleteRoom(roomId);

        // Assert
        verify(this.roomServiceSpy, times(1)).findRoomById(roomId);
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user1.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user2.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user3.getId());
        verify(this.roomRepository, times(1)).deleteById(roomId);
    }

    @Test
    public void deleteRoom_WithInexistentRoomId_ThrowsException() {
        UUID roomId = UUID.randomUUID();
        doThrow(EntityNotFoundException.class).when(roomServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomServiceSpy.deleteRoom(roomId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomServiceSpy, times(1)).findRoomById(roomId);
        verify(this.userRoomRepository, times(0)).removeUserFromRoom(anyLong());
        verify(this.roomRepository, times(0)).deleteById(any(UUID.class));
    }
}
