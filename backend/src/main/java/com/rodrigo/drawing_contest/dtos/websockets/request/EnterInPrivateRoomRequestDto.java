package com.rodrigo.drawing_contest.dtos.websockets.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnterInPrivateRoomRequestDto {
    @NotEmpty(message = "roomId must not be empty")
    private UUID roomId;
    @NotEmpty(message = "password must not be empty")
    private String roomPassword;
}
