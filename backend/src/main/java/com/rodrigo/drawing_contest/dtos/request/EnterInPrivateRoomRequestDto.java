package com.rodrigo.drawing_contest.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnterInPrivateRoomRequestDto {
    private String password;
}
