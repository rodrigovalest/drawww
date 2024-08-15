package com.rodrigo.drawing_contest.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRedis {
    private Long userId;
    private String username;
    private WaitingPlayerStatusEnum status;
    private String svg = null;
    private Long voteCount;
    private Double voteSum;

    public UserRedis(Long userId, String username) {
        this.userId = userId;
        this.username = username;
        this.status = WaitingPlayerStatusEnum.WAITING;
    }

    public enum WaitingPlayerStatusEnum {
        WAITING,
        READY
    }
}
