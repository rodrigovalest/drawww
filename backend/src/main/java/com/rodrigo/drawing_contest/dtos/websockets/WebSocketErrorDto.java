package com.rodrigo.drawing_contest.dtos.websockets;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WebSocketErrorDto<T>  {
    private String roomStatus;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public WebSocketErrorDto(String message) {
        this.roomStatus = "ERROR";
        this.message = message;
    }
}
