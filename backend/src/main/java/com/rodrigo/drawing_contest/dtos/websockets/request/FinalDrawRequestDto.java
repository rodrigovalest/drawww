package com.rodrigo.drawing_contest.dtos.websockets.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinalDrawRequestDto {
    @NotEmpty(message = "draw image must not be empty")
    private byte[] draw;
}
