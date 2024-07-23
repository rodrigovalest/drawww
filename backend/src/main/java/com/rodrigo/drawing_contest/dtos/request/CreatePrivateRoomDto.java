package com.rodrigo.drawing_contest.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePrivateRoomDto {
    @NotEmpty(message = "password must not be empty")
    private String password;
}
