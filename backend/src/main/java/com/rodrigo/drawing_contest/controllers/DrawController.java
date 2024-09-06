package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.http.request.SendUserDrawDto;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/draws")
public class DrawController {

    private final RoomManagerService roomManagerService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDrawing(
            @RequestBody @Valid SendUserDrawDto dto,
            Principal principal
    ) {
        User user = this.userService.findUserByUsername(principal.getName());
        Room room = this.roomManagerService.setUserDraw(user, dto.getSvgDraw());
        System.out.println(room);

        return ResponseEntity.ok().build();
    }
}
