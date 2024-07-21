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

    private final RoomRepository roomRepository;
    private final UserService userService;
    private final RoomService roomService;

    @PostMapping("/create")
    public void createRoom(@RequestBody Room room) {
        this.roomRepository.createRoom(room);
    }

    @GetMapping("/{roomId}")
    public Room findRoom(@PathVariable("roomId") Long roomId) {
        return this.roomRepository.findRoom(roomId);
    }

    @PostMapping("/{roomId}")
    public void enterInRoom(@PathVariable("roomId") Long roomId, @RequestBody EnterInRoomRequestDto dto) {
        User user = this.userService.findUserByUsername(dto.getUsername());
        this.roomService.enterInRoom(user, roomId, dto.getPassword());
    }
    
    @DeleteMapping
    public void leaveRoom(@RequestBody LeaveRoomRequestDto dto) {
        User user = this.userService.findUserByUsername(dto.getUsername());
        this.roomService.leaveRoom(user);
    }
}
