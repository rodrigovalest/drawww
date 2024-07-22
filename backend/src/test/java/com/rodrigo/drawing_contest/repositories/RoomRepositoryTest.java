package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.config.TestRedisConfig;
import com.rodrigo.drawing_contest.exceptions.InvalidRoomException;
import com.rodrigo.drawing_contest.exceptions.RoomAlreadyExistsException;
import com.rodrigo.drawing_contest.exceptions.RoomNotFoundException;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static com.rodrigo.drawing_contest.repositories.RoomRepository.ROOM_KEY_PREFIX;

@DataRedisTest
@Testcontainers
@Import({ TestRedisConfig.class, RoomRepository.class })
class RoomRepositoryTest {

    @Autowired
    private RedisTemplate<String, Room> redisTemplate;

    @Autowired
    private RoomRepository roomRepository;

    @Container
    private final static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeAll
    public static void before() {
        redisContainer.start();
    }

    @AfterAll
    public static void after() {
        redisContainer.stop();
    }

    @BeforeEach
    public void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void createRoom_WithValidData_ShouldCreateRoom() {
        // Arrange
        Room room = new Room(UUID.randomUUID(), "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);

        // Act
        this.roomRepository.createRoom(room);

        // Assert
        Room sut = this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + room.getId().toString());
        Assertions.assertThat(sut).isEqualTo(room);
    }

    @Test
    public void createRoom_WithAlreadyExistentRoomId_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        Room newRoom = new Room(room.getId(), "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);

        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(newRoom)).isInstanceOf(RoomAlreadyExistsException.class);
    }

    @Test
    public void createRoom_WithInvalidRoomId_ThrowException() {
        Room room = new Room(null, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNullAccessType_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "asddsadsadasda", null, RoomStatusEnum.WAITING, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNullStatus_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, null, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNullSize_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, null);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNegativeSize_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, -123L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithInvalidSize_ThrowException() {
        Room room = new Room(UUID.randomUUID(), "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, -123312L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.createRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void findRoom_WithValidRoomId_ReturnRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);

        // Act
        Room sut = this.roomRepository.findRoom(roomId);

        // Assert
        Assertions.assertThat(sut).isEqualTo(room);
    }

    @Test
    public void findRoom_WithInexistentRoomId_ThrowsException() {
        UUID roomId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> this.roomRepository.findRoom(roomId)).isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    public void updateRoom_WithValidData_ShouldUpdateRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);
        Room toUpdateRoom = new Room(roomId, room.getPassword(), room.getAccessType(), room.getStatus(), room.getSize());
        toUpdateRoom.addUser(new UserRedis(102L, "username"));
        toUpdateRoom.setStatus(RoomStatusEnum.PLAYING);

        // Act
        this.roomRepository.updateRoom(roomId, toUpdateRoom);

        // Assert
        Room sut = this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + room.getId().toString());
        Assertions.assertThat(sut).isEqualTo(toUpdateRoom);
    }

    @Test
    public void updateRoom_WithInexistentRoomId_ThrowsException() {
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.updateRoom(roomId, room)).isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    public void deleteRoom_WithValidRoomId_ShouldDeleteRoom() {
        // Arrange
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + room.getId().toString(), room);

        // Act
        this.roomRepository.deleteRoom(roomId);

        // Assert
        Assertions.assertThat(this.redisTemplate.hasKey(ROOM_KEY_PREFIX + roomId.toString()))
                .isEqualTo(false);
    }

    @Test
    public void deleteRoom_WithInexistentRoomId_ThrowsException() {
        Assertions.assertThatThrownBy(() -> this.roomRepository.deleteRoom(UUID.randomUUID()))
                .isInstanceOf(RoomNotFoundException.class);
    }
}
