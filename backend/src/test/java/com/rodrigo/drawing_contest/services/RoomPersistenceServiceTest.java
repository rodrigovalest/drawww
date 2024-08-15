package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.EntityNotFoundException;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RoomPersistenceServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @InjectMocks
    private RoomPersistenceService roomPersistenceService;

    @Spy
    @InjectMocks
    private RoomPersistenceService roomPersistenceServiceSpy;

    @Test
    public void findRoomById_WithValidData_ReturnsRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        Room room = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(new UserRedis(user.getId(), user.getUsername()));
        when(this.roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        Room sut = this.roomPersistenceService.findRoomById(roomId);

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

        Assertions.assertThatThrownBy(() -> this.roomPersistenceService.findRoomById(roomId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomRepository, times(1))
                .findById(any(UUID.class));
    }

    @Test
    public void saveRoom_WithValidData_ReturnsRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        UserRedis userRedis = new UserRedis(user.getId(), user.getUsername());
        Room toPersistRoom = new Room(null, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        Room newRoom = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        toPersistRoom.addUser(userRedis);
        newRoom.addUser(userRedis);
        when(this.roomRepository.save(toPersistRoom)).thenReturn(newRoom);

        // Act
        Room sut = this.roomPersistenceService.saveRoom(toPersistRoom);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo(newRoom);

        verify(this.roomRepository, times(1))
                .save(toPersistRoom);
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
        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);

        // Act
        this.roomPersistenceServiceSpy.deleteRoom(roomId);

        // Assert
        verify(this.roomPersistenceServiceSpy, times(1)).findRoomById(roomId);
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user1.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user2.getId());
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user3.getId());
        verify(this.roomRepository, times(1)).deleteById(roomId);
    }

    @Test
    public void deleteRoom_WithInexistentRoomId_ThrowsException() {
        UUID roomId = UUID.randomUUID();
        doThrow(EntityNotFoundException.class).when(roomPersistenceServiceSpy).findRoomById(roomId);

        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.deleteRoom(roomId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.roomPersistenceServiceSpy, times(1)).findRoomById(roomId);
        verify(this.userRoomRepository, times(0)).removeUserFromRoom(anyLong());
        verify(this.roomRepository, times(0)).deleteById(any(UUID.class));
    }

    @Test
    public void addUserToRoom_WithValidData_ShouldAddUserToRoom() {
        // Arrange
        Long userId = 10L;
        UUID roomId = UUID.randomUUID();

        // Act
        this.roomPersistenceService.addUserToRoom(userId, roomId);

        // Assert
        verify(this.userRoomRepository, times(1)).addUserToRoom(userId, roomId);
    }

    @Test
    public void removeUserFromRoom_WithValidData_ShouldRemoveUserToRoom() {
        // Arrange
        Long userId = 10L;

        // Act
        this.roomPersistenceService.removeUserFromRoom(userId);

        // Assert
        verify(this.userRoomRepository, times(1)).removeUserFromRoom(userId);
    }

    @Test
    public void getRoomIdOfUser_WithValidData_ReturnRoomId() {
        // Arrange
        Long userId = 10L;
        UUID roomId = UUID.randomUUID();
        when(this.userRoomRepository.getRoomIdOfUser(userId)).thenReturn(roomId);

        // Act
        UUID sut = this.roomPersistenceService.getRoomIdOfUser(userId);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo(roomId);

        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(userId);
    }
}
