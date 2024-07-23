package com.rodrigo.drawing_contest.dtos.mappers;

import com.rodrigo.drawing_contest.dtos.response.RoomResponseDto;
import com.rodrigo.drawing_contest.models.room.Room;

public class RoomMapper {
    public static RoomResponseDto toDto(Room room) {
        return new RoomResponseDto(room.getAccessType(), room.getStatus(), room.getSize(), room.getUsers());
    }
}
