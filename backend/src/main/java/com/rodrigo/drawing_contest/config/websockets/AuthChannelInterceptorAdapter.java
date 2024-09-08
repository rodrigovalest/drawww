package com.rodrigo.drawing_contest.config.websockets;

import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.UserService;
import com.rodrigo.drawing_contest.services.WebSocketAuthenticatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final WebSocketAuthenticatorService webSocketAuthenticatorService;
    private final RoomManagerService roomManagerService;
    private final UserService userService;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println(accessor.toString());
        System.out.println(message.toString());

        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String login = accessor.getLogin();

            final UsernamePasswordAuthenticationToken user = this.webSocketAuthenticatorService
                    .getAuthenticatedOrFail(login);

            accessor.setUser(user);
        }

        return message;
    }
}
