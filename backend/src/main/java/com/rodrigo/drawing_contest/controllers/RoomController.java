package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.request.EnterInRoomRequestDto;
import com.rodrigo.drawing_contest.dtos.request.LeaveRoomRequestDto;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.repositories.RoomRepository;
import com.rodrigo.drawing_contest.services.RoomService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final UserService userService;
    private final RoomService roomService;
}
