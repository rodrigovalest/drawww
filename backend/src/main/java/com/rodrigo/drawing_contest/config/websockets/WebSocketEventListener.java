package com.rodrigo.drawing_contest.config.websockets;

import com.rodrigo.drawing_contest.dtos.websockets.WebSocketDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.WaitingRoomUpdateResponseDto;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.RoomPersistenceService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.Instant;

@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final RoomPersistenceService roomPersistenceService;
    private final RoomManagerService roomManagerService;
    private final UserService userService;
    private final SimpMessagingTemplate template;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println(event.toString());
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        var user = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();

        if (user != null) {
            String username = user.getName();
            System.out.println("(web socket disconnected event) user {" + username + "} disconnected at " + Instant.now());
            User userEntity = this.userService.findUserByUsername(username);

            if (this.roomPersistenceService.getRoomIdOfUser(userEntity.getId()) != null) {
                Room room = this.roomManagerService.leaveRoom(userEntity);

                if (room != null && room.getStatus() == RoomStatusEnum.WAITING) {
                    WebSocketDto<WaitingRoomUpdateResponseDto> dto = new WebSocketDto<>(
                            room.getStatus(),
                            "updated room",
                            new WaitingRoomUpdateResponseDto(room.getId(), room.getUsers())
                    );
                    room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", dto));
                }
            }
        }
    }
}
