//package com.rodrigo.drawing_contest.services;
//
//import com.rodrigo.drawing_contest.exceptions.*;
//import com.rodrigo.drawing_contest.models.room.Room;
//import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
//import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
//import com.rodrigo.drawing_contest.models.user.User;
//import com.rodrigo.drawing_contest.models.user.UserRedis;
//import com.rodrigo.drawing_contest.repositories.RoomRepository;
//import com.rodrigo.drawing_contest.repositories.UserRoomRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RoomManagerServiceTest {
//
//    @Mock
//    private RoomRepository roomRepository;
//
//    @Mock
//    private UserRoomRepository userRoomRepository;
//
////    private final UserService userService;
////    private final RoomPersistenceService roomPersistenceService;
////    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
////    private final ApplicationEventPublisher eventPublisher;
////    private static final int GAME_DURATION = 1;
//
//    @Test
//    public void createPublicRoom_WithValidData_ShouldCreateRoom() {
//        // Arrange
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        Room room = new Room(UUID.randomUUID(), null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
//        room.addUser(new UserRedis(user.getId(), user.getUsername()));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//        when(this.roomRepository.save(any(Room.class))).thenReturn(room);
//
//        // Act
//        Room sut = this.roomPersistenceService.createPublicRoom(user);
//
//        // Assert
//        Assertions.assertThat(sut).isNotNull();
//        Assertions.assertThat(sut.getId()).isNotNull();
//        Assertions.assertThat(sut.getPassword()).isNull();
//        Assertions.assertThat(sut.getUsers().size()).isEqualTo(1);
//        Assertions.assertThat(sut.getAccessType()).isEqualTo(RoomAccessTypeEnum.PUBLIC);
//        Assertions.assertThat(sut.getStatus()).isEqualTo(RoomStatusEnum.WAITING);
//        Assertions.assertThat(sut.getSize()).isEqualTo(10L);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomRepository, times(1))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(1))
//                .addUserToRoom(eq(user.getId()), any(UUID.class));
//    }
//
//    @Test
//    public void createPublicRoom_WithUserAlreadyInSomeRoom_ThrowException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceService.createPublicRoom(user))
//                .isInstanceOf(UserIsAlreadyInARoomException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(eq(user.getId()), any(UUID.class));
//    }
//
//    @Test
//    public void createPrivateRoom_WithValidData_ShouldCreateRoom() {
//        // Arrange
//        String roomPassword = "roompassword";
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        Room room = new Room(UUID.randomUUID(), roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
//        room.addUser(new UserRedis(user.getId(), user.getUsername()));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//        when(this.roomRepository.save(any(Room.class))).thenReturn(room);
//
//        // Act
//        Room sut = this.roomPersistenceService.createPrivateRoom(user, roomPassword);
//
//        // Assert
//        Assertions.assertThat(sut).isNotNull();
//        Assertions.assertThat(sut.getId()).isNotNull();
//        Assertions.assertThat(sut.getPassword()).isEqualTo(roomPassword);
//        Assertions.assertThat(sut.getUsers().size()).isEqualTo(1);
//        Assertions.assertThat(sut.getAccessType()).isEqualTo(RoomAccessTypeEnum.PRIVATE);
//        Assertions.assertThat(sut.getStatus()).isEqualTo(RoomStatusEnum.WAITING);
//        Assertions.assertThat(sut.getSize()).isEqualTo(10L);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomRepository, times(1))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(1))
//                .addUserToRoom(eq(user.getId()), any(UUID.class));
//    }
//
//    @Test
//    public void createPrivateRoom_WithUserAlreadyInSomeRoom_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceService.createPrivateRoom(user, "roomPassword"))
//                .isInstanceOf(UserIsAlreadyInARoomException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(eq(user.getId()), any(UUID.class));
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithValidData_ShouldEnterInRoom() {
//        // Arrange
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//        String roomPassword = "123mudar";
//        UUID roomId = UUID.randomUUID();
//        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
//        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        // Act
//        this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword);
//
//        // Assert
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1))
//                .findRoomById(roomId);
//        room.addUser(new UserRedis(user.getId(), user.getUsername()));
//        verify(this.roomRepository, times(1))
//                .save(eq(room));
//        verify(this.userRoomRepository, times(1))
//                .addUserToRoom(user.getId(), room.getId());
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithUserIsAlreadyInARoom_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(UUID.randomUUID());
//        String roomPassword = "123mudar";
//        UUID roomId = UUID.randomUUID();
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
//                .isInstanceOf(UserIsAlreadyInARoomException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(0))
//                .findRoomById(roomId);
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(anyLong(), any(UUID.class));
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithInexistentRoom_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//
//        String roomPassword = "123mudar";
//        UUID roomId = UUID.randomUUID();
//        doThrow(EntityNotFoundException.class).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
//                .isInstanceOf(EntityNotFoundException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1))
//                .findRoomById(roomId);
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(anyLong(), any(UUID.class));
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithNonPrivateRoom_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//
//        String roomPassword = "123mudar";
//        UUID roomId = UUID.randomUUID();
//        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
//        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
//                .isInstanceOf(EntityNotFoundException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1))
//                .findRoomById(roomId);
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(anyLong(), any(UUID.class));
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithInvalidPassword_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//
//        String roomPassword = "invalidRoomPassword";
//        UUID roomId = UUID.randomUUID();
//        Room room = new Room(roomId, "roompassword", RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.WAITING, 10L);
//        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
//                .isInstanceOf(RoomPasswordDontMatchException.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1))
//                .findRoomById(roomId);
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(anyLong(), any(UUID.class));
//    }
//
//    @Test
//    public void enterInPrivateRoom_WithMatchAlreadyStarted_ThrowsException() {
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//
//        String roomPassword = "123mudar";
//        UUID roomId = UUID.randomUUID();
//        Room room = new Room(roomId, roomPassword, RoomAccessTypeEnum.PRIVATE, RoomStatusEnum.PLAYING, 10L);
//        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.enterInPrivateRoom(user, roomId, roomPassword))
//                .isInstanceOf(RoomNotAvailable.class);
//
//        verify(this.userRoomRepository, times(1))
//                .getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1))
//                .findRoomById(roomId);
//        verify(this.roomRepository, times(0))
//                .save(any(Room.class));
//        verify(this.userRoomRepository, times(0))
//                .addUserToRoom(anyLong(), any(UUID.class));
//    }
//
//    @Test
//    public void leaveRoom_WithValidData_ShouldRemoveUserFromRoom() {
//        // Arrange
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        UUID roomId = UUID.randomUUID();
//        Room room = new Room(roomId, null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
//        room.addUser(new UserRedis(user.getId(), user.getUsername()));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(roomId);
//        doReturn(room).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        // Act
//        this.roomPersistenceServiceSpy.leaveRoom(user);
//
//        // Assert
//        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1)).findRoomById(roomId);
//        room.removeUser(user.getId());
//        verify(this.roomRepository, times(1)).save(eq(room));
//        verify(this.userRoomRepository, times(1)).removeUserFromRoom(user.getId());
//    }
//
//    @Test
//    public void leaveRoom_WithUserNotInAnyRoom_ThrowsException() {
//        // Arrange
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(null);
//
//        // Act & Assert
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.leaveRoom(user))
//                .isInstanceOf(UserIsNotInAnyRoomException.class);
//
//        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(0)).findRoomById(any(UUID.class));
//        verify(this.roomRepository, times(0)).save(any(Room.class));
//        verify(this.userRoomRepository, times(0)).removeUserFromRoom(user.getId());
//    }
//
//    @Test
//    public void leaveRoom_WithInexistentRoom_ThrowsException() {
//        // Arrange
//        User user = new User(10L, "cleiton", "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
//        UUID roomId = UUID.randomUUID();
//        when(this.userRoomRepository.getRoomIdOfUser(user.getId())).thenReturn(roomId);
//        doThrow(EntityNotFoundException.class).when(roomPersistenceServiceSpy).findRoomById(roomId);
//
//        // Act & Assert
//        Assertions.assertThatThrownBy(() -> this.roomPersistenceServiceSpy.leaveRoom(user))
//                .isInstanceOf(EntityNotFoundException.class);
//
//        verify(this.userRoomRepository, times(1)).getRoomIdOfUser(user.getId());
//        verify(this.roomPersistenceServiceSpy, times(1)).findRoomById(roomId);
//        verify(this.roomRepository, times(0)).save(any(Room.class));
//        verify(this.userRoomRepository, times(0)).removeUserFromRoom(user.getId());
//    }
//
//}
