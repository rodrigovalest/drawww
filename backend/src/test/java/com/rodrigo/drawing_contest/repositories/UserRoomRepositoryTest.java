package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.config.TestRedisConfig;
import com.rodrigo.drawing_contest.exceptions.UserIsAlreadyInARoomException;
import com.rodrigo.drawing_contest.exceptions.UserIsNotInAnyRoomException;
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

import static com.rodrigo.drawing_contest.repositories.UserRoomRepository.USER_ROOM_KEY_PREFIX;

@DataRedisTest
@Testcontainers
@Import({ TestRedisConfig.class, UserRoomRepository.class })
public class UserRoomRepositoryTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRoomRepository userRoomRepository;

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
        Long roomId = 1234L;

        // Act
        this.userRoomRepository.addUserToRoom(userId, roomId);

        // Assert
        Long sut = Long.parseLong(this.redisTemplate.opsForValue().get(USER_ROOM_KEY_PREFIX + userId));
        Assertions.assertThat(sut).isEqualTo(roomId);
    }

    @Test
    public void addUserToRoom_WithUserWhoIsAlreadyInARoom_ThrowsException() {
        Long userId = 23L;
        Long roomId = 1234L;
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, String.valueOf(3452L));

        Assertions.assertThatThrownBy(() -> this.userRoomRepository.addUserToRoom(userId, roomId))
                .isInstanceOf(UserIsAlreadyInARoomException.class);
    }

    @Test
    public void getRoomIdOfUser_WithValidData_ReturnsLong() {
        // Arrange
        Long userId = 23L;
        Long roomId = 1234L;
        this.redisTemplate.opsForValue().set(USER_ROOM_KEY_PREFIX + userId, roomId.toString());

        // Act
        Long sut = this.userRoomRepository.getRoomIdOfUser(userId);

        // Assert
        Assertions.assertThat(sut).isEqualTo(roomId);
    }

    @Test
    public void getRoomIdOfUser_WithUserNotInAnyRoom_ThrowsException() {
        Assertions.assertThatThrownBy(() -> this.userRoomRepository.getRoomIdOfUser(123344L))
                .isInstanceOf(UserIsNotInAnyRoomException.class);
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
    public void removeUserFromRoom_WithUserNotInAnyRoom_ThrowsException() {
        Assertions.assertThatThrownBy(() -> this.userRoomRepository.removeUserFromRoom(123344L))
                .isInstanceOf(UserIsNotInAnyRoomException.class);
    }
}
