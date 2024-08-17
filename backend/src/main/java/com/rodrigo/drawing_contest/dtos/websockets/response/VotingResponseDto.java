package com.rodrigo.drawing_contest.dtos.websockets.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class VotingResponseDto {
    private String drawSvg;
    private String targetUsername;
}
