package com.rodrigo.drawing_contest.dtos.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponseDto {
    private String token;
}
