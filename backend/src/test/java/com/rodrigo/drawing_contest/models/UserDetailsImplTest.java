package com.rodrigo.drawing_contest.models;

import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserDetailsImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsImplTest {

    @Mock
    private User user;

    @InjectMocks
    private UserDetailsImpl userDetailsImpl;

    @Test
    public void getAuthorities_WithValidData_ShouldReturnUser() {
        Collection<? extends GrantedAuthority> sut = this.userDetailsImpl.getAuthorities();

        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).extracting(GrantedAuthority::getAuthority)
                .containsExactly("USER");
    }

    @Test
    public void getPassword_WithValidData_ShouldReturnPassword() {
        // Arrange
        when(this.user.getPassword()).thenReturn("mockedPassword");

        // Act
        String sut = this.userDetailsImpl.getPassword();

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo("mockedPassword");
    }

    @Test
    public void getUsername_WithValidData_ShouldReturnUsername() {
        // Arrange
        when(this.user.getUsername()).thenReturn("mockedUsername");

        // Act
        String sut = this.userDetailsImpl.getUsername();

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo("mockedUsername");
    }
}
