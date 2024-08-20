package com.rodrigo.drawing_contest.dtos.websockets.response;

import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class WaitingRoomUpdateResponseDto {
    private List<UserRedis> users = new ArrayList<UserRedis>();
}
