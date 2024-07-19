package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.security.jwt.secret-key}")
    public String SECRET_KEY;
    private final String JWT_BEARER = "Bearer ";
    private final String JWT_AUTHORIZATION = "Authorization";
    private final long EXPIRE_DAYS = 0;
    private final long EXPIRE_HOURS = 2;
    private final long EXPIRE_MINUTES = 30;

    public String createToken(User user) {
        Date issuedAt = new Date();
        Date limit = this.generateToExpireDate(issuedAt);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(limit)
                .signWith(
                        Keys.hmacShaKeyFor(this.SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(this.SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(this.refactorToken(token));

            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUsernameByToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(this.SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(refactorToken(token))
                    .getBody();

            return (claims != null) ? claims.getSubject() : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String refactorToken(String token) {
        return token.startsWith(this.JWT_BEARER) ? token.substring(this.JWT_BEARER.length()) : token;
    }

    private Date generateToExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(this.EXPIRE_DAYS).plusHours(this.EXPIRE_HOURS).plusMinutes(this.EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }
}
