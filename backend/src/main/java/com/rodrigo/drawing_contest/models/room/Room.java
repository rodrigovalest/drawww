package com.rodrigo.drawing_contest.models.room;

import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Room {
    private UUID id;
    private String password;
    private RoomAccessTypeEnum accessType;
    private RoomStatusEnum status;
    private Long size;
    private final List<UserRedis> users = new ArrayList<UserRedis>();

    public void addUser(UserRedis user) {
        this.users.add(user);
    }

    public void removeUser(Long userId) {
        this.users.removeIf(user -> user.getId().equals(userId));
    }
}
