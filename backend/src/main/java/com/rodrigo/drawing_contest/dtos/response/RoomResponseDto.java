package com.rodrigo.drawing_contest.dtos.response;

import com.rodrigo.drawing_contest.models.room.RoomAccessTypeEnum;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomResponseDto {
    private RoomAccessTypeEnum accessType;
    private RoomStatusEnum status;
    private Long size;
    private List<UserRedis> users = new ArrayList<UserRedis>();
}
