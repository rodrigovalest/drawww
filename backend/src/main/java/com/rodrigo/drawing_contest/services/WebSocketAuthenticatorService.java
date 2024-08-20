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

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(
            final String  username, final String password
    ) throws AuthenticationException {
        User user = this.userService.findUserByUsernameAndPassword(username, password);

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                Collections.singleton((GrantedAuthority) () -> "USER")
        );
    }
}
