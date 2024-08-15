package com.rodrigo.drawing_contest.dtos.websockets.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoteRequestDto {
    @NotEmpty(message = "username must not be empty")
    private String username;
    @NotNull(message = "rate must not be null")
    @Min(value = 1)
    @Max(value = 5)
    private Long rate;
}
