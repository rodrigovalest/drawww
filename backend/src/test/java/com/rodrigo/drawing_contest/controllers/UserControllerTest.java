package com.rodrigo.drawing_contest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigo.drawing_contest.config.security.SecurityConfig;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserDetails;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.UserService;
import com.rodrigo.drawing_contest.dtos.request.LoginRequestDto;
import com.rodrigo.drawing_contest.dtos.request.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private HttpServletRequest request;

    @Test
    public void register_WithValidData_Return201Created() throws Exception {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("user123", "123mudar");
        User user = new User(null, registerRequestDto.getUsername(), "encryptedPassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        when(this.userService.createUser(registerRequestDto.getUsername(), registerRequestDto.getPassword()))
                .thenReturn(user);

        // Act
        ResultActions response = this.mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        // Assert
        response.andExpect(status().isCreated());
        verify(this.userService, times(1)).createUser(registerRequestDto.getUsername(), registerRequestDto.getPassword());
    }

    @Test
    public void register_WithValidData_Return422UnprocessableEntity() throws Exception {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(null, null);

        // Act
        ResultActions response = this.mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        // Assert
        response.andExpect(status().isUnprocessableEntity());
        verify(this.userService, times(0)).createUser(anyString(), anyString());
    }

    @Test
    public void register_WithAnUnknowError_Return500InternalServerError() throws Exception {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto("user123", "123mudar");
        when(this.userService.createUser(registerRequestDto.getUsername(), registerRequestDto.getPassword())).thenThrow(RuntimeException.class);

        // Act
        ResultActions response = this.mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)));

        // Assert
        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void login_WithValidData_Returns200OK() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("user123", "123mudar");
        User user = new User(null, loginRequestDto.getUsername(), "encryptedPassword", LocalDateTime.now(), LocalDateTime.now());
        UserDetails userDetails = new UserDetails(user);

        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());

        when(this.authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(this.jwtService.createToken(user)).thenReturn("fake-jwt-token");

        // Act
        ResultActions response = this.mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(this.authenticationManager, times(1)).authenticate(authenticationToken);
        verify(this.jwtService, times(1)).createToken(user);
    }

    @Test
    public void login_WithInvalidData_Returns401Unauthorized() throws Exception {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("invalidUser", "wrongPassword");
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        when(this.authenticationManager.authenticate(authenticationToken)).thenThrow(BadCredentialsException.class);

        // Act
        ResultActions response = this.mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        // Assert
        response.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "123mudar", password = "123mudar")
    public void helloWorld() throws Exception {
        // Act
        ResultActions response = this.mockMvc.perform(get("/api/v1/users/hello")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk());
    }
}
