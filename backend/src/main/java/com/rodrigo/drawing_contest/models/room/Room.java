package com.rodrigo.drawing_contest.models.room;

import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@RedisHash("room")
public class Room {
    private UUID id;
    private String password;
    private RoomAccessTypeEnum accessType;
    private RoomStatusEnum status;
    private Long size;
    private String theme;
    private Instant startTimePlaying;
    private Instant endTimePlaying;
    private Instant startTimeVoting;
    private Instant endTimeVoting;
    private Integer currentVotingIndex = 0;
    private List<UserRedis> users = new ArrayList<UserRedis>();

    public Room(UUID id, String password, RoomAccessTypeEnum accessType, RoomStatusEnum status, Long size) {
        this.id = id;
        this.password = password;
        this.accessType = accessType;
        this.status = status;
        this.size = size;
    }

    public void addUser(UserRedis user) {
        this.users.add(user);
    }

    public void removeUser(Long userId) {
        this.users.removeIf(user -> user.getUserId().equals(userId));
    }
}
