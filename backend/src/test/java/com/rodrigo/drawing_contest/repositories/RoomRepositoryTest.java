package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.config.TestRedisConfig;
import com.rodrigo.drawing_contest.exceptions.InvalidRoomException;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
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
    public void saveRoom_WithValidData_ShouldCreateRoom() {
        // Arrange
        Room room = new Room(null, "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);

        // Act
        this.roomRepository.saveRoom(room);

        // Assert
        Room sut = this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + room.getId().toString());
        Assertions.assertThat(sut).isEqualTo(room);
    }

    @Test
    public void saveRoom_WithAlreadyPersistedRoomId_ShouldOverrideRoom() {
        // Arrange
        Room persistedRoom = new Room(UUID.randomUUID(), "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        this.redisTemplate.opsForValue().set(ROOM_KEY_PREFIX + persistedRoom.getId().toString(), persistedRoom);
        Room room = new Room(persistedRoom.getId(), "password", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.PLAYING, 10L);

        // Act
        this.roomRepository.saveRoom(room);

        // Assert
        Room sut = this.redisTemplate.opsForValue().get(ROOM_KEY_PREFIX + room.getId().toString());
        Assertions.assertThat(sut).isEqualTo(room);
    }

    @Test
    public void createRoom_WithNullAccessType_ThrowException() {
        Room room = new Room(null, "asddsadsadasda", null, RoomStatusEnum.WAITING, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.saveRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNullStatus_ThrowException() {
        Room room = new Room(null, "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, null, 10L);
        Assertions.assertThatThrownBy(() -> this.roomRepository.saveRoom(room)).isInstanceOf(InvalidRoomException.class);
    }

    @Test
    public void createRoom_WithNullSize_ThrowException() {
        Room room = new Room(null, "asddsadsadasda", RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, null);
        Assertions.assertThatThrownBy(() -> this.roomRepository.saveRoom(room)).isInstanceOf(InvalidRoomException.class);
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
    public void findRoom_WithInexistentRoomId_ReturnsNull() {
        // Arrange
        UUID roomId = UUID.randomUUID();

        // Act
        Room sut = this.roomRepository.findRoom(roomId);

        // Assert
        Assertions.assertThat(sut).isNull();
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
        // Arrange
        UUID roomId = UUID.randomUUID();

        // Act
        this.roomRepository.deleteRoom(roomId);

        // Assert
        Assertions.assertThat(this.redisTemplate.hasKey(ROOM_KEY_PREFIX + roomId.toString()))
                .isEqualTo(false);
    }
}
