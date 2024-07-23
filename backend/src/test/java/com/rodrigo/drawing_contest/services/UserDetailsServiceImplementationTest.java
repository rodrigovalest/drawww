package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.EntityNotFoundException;
import com.rodrigo.drawing_contest.models.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplementationTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImplementation userDetailsServiceImplementation;

    @Test
    public void loadUserByUsername_WithValidUsername_ReturnUserDetails() {
        // Arrange
        String username = "cleiton";
        User persistedUser = new User(10L, username, "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userService.findUserByUsername(username)).thenReturn(persistedUser);

        // Act
        UserDetails sut = this.userDetailsServiceImplementation.loadUserByUsername(username);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getUsername()).isEqualTo(username);

        verify(this.userService, times(1)).findUserByUsername(username);
    }

    @Test
    public void loadUserByUsername_WithInexistentUsername_ThrowsException() {
        // Arrange
        String username = "cleiton";
        when(this.userService.findUserByUsername(username)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThatThrownBy(() -> this.userDetailsServiceImplementation.loadUserByUsername(username))
                .isInstanceOf(EntityNotFoundException.class);

        verify(this.userService, times(1)).findUserByUsername(username);
    }
}
