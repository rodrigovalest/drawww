package com.rodrigo.drawing_contest.security;

import com.rodrigo.drawing_contest.config.security.SecurityFilter;
import com.rodrigo.drawing_contest.services.JwtService;
import com.rodrigo.drawing_contest.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SecurityFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SecurityFilter securityFilter;

    @Mock
    private HttpServletRequest request;

//    @Test
//    public void recoverToken_WithValidToken_ReturnToken() {
//        // Arrange
//        String token = "validToken";
//        String authHeader = "Bearer " + token;
//        when(request.getHeader("Authorization")).thenReturn(authHeader);
//
//        // Act
//        String result = this.securityFilter.recoverToken(request);
//
//        // Assert
//        Assertions.assertThat(result).isNotNull();
//        Assertions.assertThat(result).isEqualTo(token);
//    }
}
