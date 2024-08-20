package com.rodrigo.drawing_contest.dtos.http.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequestDto {
    @NotEmpty(message = "username must not be empty")
    private String username;
    @NotEmpty(message = "password must not be empty")
    private String password;
}
