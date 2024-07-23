package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.request.CreatePrivateRoomDto;
import com.rodrigo.drawing_contest.dtos.request.EnterInPrivateRoomRequestDto;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.services.RoomService;
import com.rodrigo.drawing_contest.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final UserService userService;
    private final RoomService roomService;

    @PostMapping("/private")
    public ResponseEntity<Void> createPrivateRoom(@RequestBody @Valid CreatePrivateRoomDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = ((com.rodrigo.drawing_contest.models.user.UserDetails) userDetails).getUser();

        Room room = this.roomService.createPrivateRoom(authenticatedUser, dto.getPassword());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(room.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/private/enter/{room_id}")
    public ResponseEntity<Void> enterInPrivateRoom(
            @PathVariable("room_id") UUID roomId,
            @RequestBody @Valid EnterInPrivateRoomRequestDto dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User authenticatedUser = ((com.rodrigo.drawing_contest.models.user.UserDetails) userDetails).getUser();

        this.roomService.enterInPrivateRoom(authenticatedUser, roomId, dto.getPassword());

        return ResponseEntity.noContent().build();
    }
}
