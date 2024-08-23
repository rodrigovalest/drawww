package com.rodrigo.drawing_contest.config.websockets;

import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.RoomPersistenceService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final RoomPersistenceService roomPersistenceService;
    private final RoomManagerService roomManagerService;
    private final UserService userService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        var user = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();

        if (user != null) {
            String username = user.getName();
            System.out.println("User disconnected: " + username);
            User userEntity = this.userService.findUserByUsername(username);

            if (this.roomPersistenceService.getRoomIdOfUser(userEntity.getId()) != null)
                this.roomManagerService.leaveRoom(userEntity);
        }
    }
}
