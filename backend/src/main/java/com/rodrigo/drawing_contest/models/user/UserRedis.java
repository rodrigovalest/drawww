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
    private WaitingPlayerStatusEnum status = WaitingPlayerStatusEnum.WAITING;
    private String svgDraw = null;
    private Long voteCount = 0L;
    private Double voteSum = 0.0;
    private Double voteResult = 0.0;
    private boolean votedInCurrentDraw = false;

    public UserRedis(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public enum WaitingPlayerStatusEnum {
        WAITING,
        READY
    }
}
