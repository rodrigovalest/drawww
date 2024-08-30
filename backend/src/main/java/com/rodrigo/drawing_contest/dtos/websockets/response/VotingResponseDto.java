package com.rodrigo.drawing_contest.dtos.websockets.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class VotingResponseDto {
    private byte[] drawSvg;
    private String targetUsername;
    private String theme;
    private Instant startTime;
    private Instant endTime;
}
