package com.rodrigo.drawing_contest.web.controllers;

import com.rodrigo.drawing_contest.models.entities.User;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.UserService;
import com.rodrigo.drawing_contest.web.dtos.request.LoginRequestDto;
import com.rodrigo.drawing_contest.web.dtos.request.RegisterRequestDto;
import com.rodrigo.drawing_contest.web.dtos.response.LoginResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        var auth = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        var userDetails = (com.rodrigo.drawing_contest.models.security.UserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

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
    public String hello() {
        return "hello world";
    }
}
