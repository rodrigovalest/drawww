package com.rodrigo.drawing_contest.security;

import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserDetails;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsTest {

    @Mock
    private User user;

    @InjectMocks
    private UserDetails userDetails;

    @Test
    public void getAuthorities_WithValidData_ShouldReturnEmptyList() {
        Collection<? extends GrantedAuthority> sut = this.userDetails.getAuthorities();

        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEmpty();
    }

    @Test
    public void getPassword_WithValidData_ShouldReturnPassword() {
        // Arrange
        when(this.user.getPassword()).thenReturn("mockedPassword");

        // Act
        String sut = this.userDetails.getPassword();

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo("mockedPassword");
    }

    @Test
    public void getUsername_WithValidData_ShouldReturnUsername() {
        // Arrange
        when(this.user.getUsername()).thenReturn("mockedUsername");

        // Act
        String sut = this.userDetails.getUsername();

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo("mockedUsername");
    }
}
