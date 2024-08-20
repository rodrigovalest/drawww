package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.config.TestRedisConfig;
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

import static com.rodrigo.drawing_contest.repositories.UserRoomRepositoryImpl.USER_ROOM_KEY_PREFIX;

@DataRedisTest
@Testcontainers
@Import({ TestRedisConfig.class, UserRoomRepositoryImpl.class })
public class UserRoomRepositoryImplTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRoomRepositoryImpl userRoomRepository;

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
    public void addUserToRoom_WithValidData_ShouldAddUserToRoom() {
        // Arrange
        Long userId = 23L;
        UUID roomId = UUID.randomUUID();

        // Act
        this.userRoomRepository.addUserToRoom(userId, roomId);

        // Assert
        UUID sut = UUID.fromString(this.redisTemplate.opsForValue().get(USER_ROOM_KEY_PREFIX + userId));
        Assertions.assertThat(sut).isEqualTo(roomId);
    }

    @Test
    public void addUserToRoom_WithUserWhoIsAlreadyInARoom_ShouldOverrideRoomId() {
        // Arrange
        Long userId = 23L;
        UUID roomId = UUID.randomUUID();
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, String.valueOf(UUID.randomUUID()));

        // Act
        this.userRoomRepository.addUserToRoom(userId, roomId);

        // Assert
        UUID sut = UUID.fromString(this.redisTemplate.opsForValue().get(USER_ROOM_KEY_PREFIX + userId));
        Assertions.assertThat(sut).isEqualTo(roomId);
    }

    @Test
    public void getRoomIdOfUser_WithValidData_ReturnsLong() {
        // Arrange
        Long userId = 23L;
        UUID roomId = UUID.randomUUID();
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, roomId.toString());

        // Act
        UUID sut = this.userRoomRepository.getRoomIdOfUser(userId);

        // Assert
        Assertions.assertThat(sut).isEqualTo(roomId);
    }

    @Test
    public void getRoomIdOfUser_WithUserNotInAnyRoom_ReturnsNull() {
        // Arrange
        Long userId = 23L;

        // Act
        UUID sut = this.userRoomRepository.getRoomIdOfUser(userId);

        // Assert
        Assertions.assertThat(sut).isNull();
    }

    @Test
    public void removeUserFromRoom_WithValidUserId_ShouldRemoveUser() {
        // Arrange
        Long userId = 23L;
        Long roomId = 1234L;
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, roomId.toString());

        // Act
        this.userRoomRepository.removeUserFromRoom(userId);

        // Assert
        Assertions.assertThat(this.redisTemplate.hasKey(USER_ROOM_KEY_PREFIX + userId)).isFalse();
    }

    @Test
    public void removeUserFromRoom_WithInexistentUserId_ShouldRemoveNothing() {
        // Arrange
        Long userId = 23L;

        // Act
        this.userRoomRepository.removeUserFromRoom(userId);

        // Assert
        Assertions.assertThat(this.redisTemplate.hasKey(USER_ROOM_KEY_PREFIX + userId)).isFalse();
    }
}
