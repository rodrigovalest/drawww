package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.exceptions.EntityNotFoundException;
import com.rodrigo.drawing_contest.exceptions.UsernameAlreadyUsedException;
import com.rodrigo.drawing_contest.models.entities.User;
import com.rodrigo.drawing_contest.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void findUserByUsername_WithValidUsername_ReturnsUser() {
        // Arrange
        String username = "cleiton";
        User persistedUser = new User(10L, username, "encryptedpassword", LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));
        when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(persistedUser));

        // Act
        User sut = this.userService.findUserByUsername(username);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getUsername()).isEqualTo(username);

        verify(this.userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void findUserByUsername_WithInexistentUsername_ThrowsException() {
        String username = "cleiton";
        when(this.userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> this.userService.findUserByUsername(username)).isInstanceOf(EntityNotFoundException.class);

        verify(this.userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void createUser_WithValidData_ReturnsUser() {
        // Arrange
        String username = "cleiton";
        String password = "123mudar";
        String encryptedPassword = "encryptedPassword";
        User savedUser = new User(123L, username, encryptedPassword, LocalDateTime.of(2024, 1, 15, 10, 30), LocalDateTime.of(2024, 1, 15, 10, 30));

        when(this.userRepository.existsByUsername(username)).thenReturn(false);
        when(this.userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User sut = this.userService.createUser(username, password);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getUsername()).isEqualTo(username);
        Assertions.assertThat(sut.getPassword()).isEqualTo(encryptedPassword);

        verify(this.userRepository, times(1)).existsByUsername(username);
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void createUser_WithAlreadyUsedUsername_ThrowsException() {
        String username = "cleiton";
        String password = "123mudar";
        when(this.userRepository.existsByUsername(username)).thenReturn(true);

        Assertions.assertThatThrownBy(() -> this.userService.createUser(username, password)).isInstanceOf(UsernameAlreadyUsedException.class);

        verify(this.userRepository, times(1)).existsByUsername(username);
        verify(this.userRepository, times(0)).save(any(User.class));
    }
}
