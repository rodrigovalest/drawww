package com.rodrigo.drawing_contest.dtos.websockets.response;

import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class CreateRoomResponseDto {
    private UUID roomId;
    private List<UserRedis> users = new ArrayList<UserRedis>();
}
