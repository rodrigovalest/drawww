package com.rodrigo.drawing_contest.dtos.websockets.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class StartingMatchResponseDto {
    private String theme;
    private Instant startTime;
    private Instant endTime;
}
