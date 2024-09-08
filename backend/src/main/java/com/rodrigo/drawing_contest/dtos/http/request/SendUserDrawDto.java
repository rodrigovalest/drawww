package com.rodrigo.drawing_contest.dtos.http.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendUserDrawDto {
    @NotEmpty(message = "draw image must not be empty")
    private String svgDraw;
}
