package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserDetailsImpl;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.UserService;
import com.rodrigo.drawing_contest.dtos.request.LoginRequestDto;
import com.rodrigo.drawing_contest.dtos.request.RegisterRequestDto;
import com.rodrigo.drawing_contest.dtos.response.LoginResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        User user = this.userService.findUserByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        String token = this.jwtService.createToken(user);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequestDto dto) {
        User user = this.userService.createUser(dto.getUsername(), dto.getPassword());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(Authentication authentication) {
        System.out.println(authentication.toString());
        return ResponseEntity.ok("HelloWorld");
    }
}
