package com.rodrigo.drawing_contest.models;

import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class RoomTest {

    @Test
    public void addUser_WithValidData_ShouldAddUserToList() {
        // Arrange
        UserRedis userRedis = new UserRedis(10L, "username x");
        Room room = new Room(UUID.randomUUID(), null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);

        // Act
        room.addUser(userRedis);

        // Assert
        Assertions.assertThat(room.getUsers().contains(userRedis)).isTrue();
    }

    @Test
    public void removeUser_WithValidData_ShouldAddUserToList() {
        // Arrange
        UserRedis userRedis = new UserRedis(10L, "username x");
        Room room = new Room(UUID.randomUUID(), null, RoomAccessTypeEnum.PUBLIC, RoomStatusEnum.WAITING, 10L);
        room.addUser(userRedis);

        // Act
        room.removeUser(userRedis.getId());

        // Assert
        Assertions.assertThat(room.getUsers().contains(userRedis)).isFalse();
    }
}
