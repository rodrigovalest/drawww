package com.rodrigo.drawing_contest.dtos.websockets.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class EnterInPrivateRoomResponseDto {
    private UUID roomId;
}
