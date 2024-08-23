package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.websockets.WebSocketErrorDto;
import com.rodrigo.drawing_contest.events.UserInactivityEvent;
import com.rodrigo.drawing_contest.exceptions.*;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@RequiredArgsConstructor
@ControllerAdvice
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate template;

    @MessageExceptionHandler
    public void runtimeExceptionHandler(RuntimeException ex, Principal principal) {
        ex.printStackTrace();
        String username = principal.getName();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageExceptionHandler
    public void userIsAlreadyInARoomExceptionHandler(UserIsAlreadyInARoomException ex, Principal principal) {
        String username = principal.getName();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageExceptionHandler
    public void actionDoNotMatchWithRoomStatusExceptionHandler(ActionDoNotMatchWithRoomStatusException ex, Principal principal) {
        String username = principal.getName();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageExceptionHandler
    public void userIsNotInAnyRoomExceptionHandler(UserIsNotInAnyRoomException ex, Principal principal) {
        String username = principal.getName();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageExceptionHandler
    public void roomPasswordDontMatchExceptionHandler(RoomPasswordDontMatchException ex, Principal principal) {
        String username = principal.getName();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageExceptionHandler
    public void userInactivityExceptionHandler(UserInactivityException ex) {
        System.out.println("websocket exception handler: " + ex.toString());
        String username = ex.getEvent().getUsername();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>(ex.getMessage());
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @EventListener
    public void handleUserInactivity(UserInactivityEvent event) {
        String username = event.getUsername();
        WebSocketErrorDto<?> responseDto = new WebSocketErrorDto<>("user disconnected by inactivity");
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }
}
