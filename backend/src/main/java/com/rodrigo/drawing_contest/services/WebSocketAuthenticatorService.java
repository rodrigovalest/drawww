package com.rodrigo.drawing_contest.services;

import com.rodrigo.drawing_contest.models.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class WebSocketAuthenticatorService {

    private final UserService userService;
    private final JwtService jwtService;

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(
            final String bearerToken
    ) throws AuthenticationException {
        try {
            String username = this.jwtService.getUsernameByToken(bearerToken);
            User user = this.userService.findUserByUsername(username);

            return new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    null,
                    Collections.singleton((GrantedAuthority) () -> "USER")
            );
        } catch (Exception e) {
            throw new AuthenticationException("authentication fails because token is invalid") {};
        }
    }
}
