package com.rodrigo.drawing_contest.dtos.websockets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WebSocketDto<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoomStatusEnum roomStatus;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public WebSocketDto(String message) {
        this.message = message;
    }

    public WebSocketDto(RoomStatusEnum roomStatus, String message) {
        this.roomStatus = roomStatus;
        this.message = message;
    }
}
