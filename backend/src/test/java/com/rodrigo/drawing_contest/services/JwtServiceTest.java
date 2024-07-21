package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.models.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field secretKeyField = JwtService.class.getDeclaredField("SECRET_KEY");
        secretKeyField.setAccessible(true);
        secretKeyField.set(this.jwtService, "7e70624fa5ae7f062346d4b4cfec4bda8c2c5404c65af140541882179aa9c924");
    }

    @Test
    public void createToken_WithValidUser_ReturnValidJwtToken() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");

        // Act
        String sut = this.jwtService.createToken(user);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).startsWith("eyJ");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(this.jwtService.SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(sut)
                .getBody();

        Assertions.assertThat(claims.getSubject()).isEqualTo("testuser");
        Assertions.assertThat(claims.getIssuedAt()).isNotNull();
        Assertions.assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    public void validateToken_WithValidToken_ReturnTrue() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        String token = this.jwtService.createToken(user);

        // Act
        boolean sut = this.jwtService.validateToken(token);

        // Assert
        Assertions.assertThat(sut).isTrue();
    }

    @Test
    public void validateToken_WithInvalidToken_ReturnFalse() {
        // Arrange
        String token = "asdmasdlasdksadkdalslsadlasdldasllsadldaslsdaldaslladsdsaasd.asdkdaskadsk.asdkasdkasdkads";

        // Act
        boolean sut = this.jwtService.validateToken(token);

        // Assert
        Assertions.assertThat(sut).isFalse();
    }

    @Test
    public void getUsernameByToken_WithValidToken_ReturnUsername() {
        // Arrange
        String username = "cleiton";
        User user = new User();
        user.setUsername(username);
        String token = this.jwtService.createToken(user);

        // Act
        String sut = this.jwtService.getUsernameByToken(token);

        // Assert
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut).isEqualTo(username);
    }

    @Test
    public void getUsernameByToken_WithNullToken_ReturnsNull() {
        // Arrange
        String token = null;

        // Act
        String sut = this.jwtService.getUsernameByToken(token);

        // Assert
        Assertions.assertThat(sut).isNull();
    }

    @Test
    public void getUsernameByToken_WithEmptyToken_ReturnsNull() {
        // Arrange
        String token = "";

        // Act
        String sut = this.jwtService.getUsernameByToken(token);

        // Assert
        Assertions.assertThat(sut).isNull();
    }

    @Test
    public void getUsernameByToken_WithValidTokenAndNullUsername_ReturnsNull() {
        // Arrange
        String username = null;
        User user = new User();
        user.setUsername(username);
        String token = this.jwtService.createToken(user);

        // Act
        String sut = this.jwtService.getUsernameByToken(token);

        // Assert
        Assertions.assertThat(sut).isNull();
    }
}
