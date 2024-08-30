package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.websockets.WebSocketDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.CreatePrivateRoomRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.EnterInPrivateRoomRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.FinalDrawRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.VoteRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.*;
import com.rodrigo.drawing_contest.events.StartPlayingEvent;
import com.rodrigo.drawing_contest.events.StartResultEvent;
import com.rodrigo.drawing_contest.events.StartingVotingForNextDrawingEvent;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.room.RoomStatusEnum;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.RoomPersistenceService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class GameController {

    private final SimpMessagingTemplate template;
    private final RoomManagerService roomManagerService;
    private final RoomPersistenceService roomPersistenceService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @MessageMapping("/rooms/private/create")
    public void createPrivateRoom(CreatePrivateRoomRequestDto requestDto, Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.createPrivateRoom(user, requestDto.getRoomPassword());

        WebSocketDto<CreateRoomResponseDto> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "room {" + room.getId() + "} succesfully created",
                new CreateRoomResponseDto(room.getId(), room.getUsers())
        );
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @MessageMapping("/rooms/private/enter")
    public void enterInPrivateRoom(EnterInPrivateRoomRequestDto requestDto, Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.enterInPrivateRoom(user, requestDto.getRoomId(), requestDto.getRoomPassword());

        WebSocketDto<EnterInPrivateRoomResponseDto> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "user succesfully entered in room",
                new EnterInPrivateRoomResponseDto(requestDto.getRoomId())
        );
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);

        WebSocketDto<WaitingRoomUpdateResponseDto> updatedRoomDto = new WebSocketDto<>(
                room.getStatus(),
                "updated room",
                new WaitingRoomUpdateResponseDto(room.getId(), room.getUsers())
        );
        for (UserRedis userRedis : room.getUsers())
            this.template.convertAndSendToUser(userRedis.getUsername(), "/queue/reply", updatedRoomDto);
    }

    @MessageMapping("/rooms/leave")
    public void leaveRoom(Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.leaveRoom(user);

        WebSocketDto<?> responseDto = new WebSocketDto<>("user succesfully leaves room");
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);

        if (room != null && room.getStatus() == RoomStatusEnum.WAITING) {
            WebSocketDto<WaitingRoomUpdateResponseDto> updatedRoomDto = new WebSocketDto<>(
                    room.getStatus(),
                    "updated room",
                    new WaitingRoomUpdateResponseDto(room.getId(), room.getUsers())
            );
            room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", updatedRoomDto));
        }
    }

    @MessageMapping("/rooms/user_status")
    public void changeUserStatus(Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.changeUserStatus(user);

        WebSocketDto<?> responseDto = new WebSocketDto<>(room.getStatus(), "succesfully change user status");
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);

        WebSocketDto<WaitingRoomUpdateResponseDto> updatedRoomDto = new WebSocketDto<>(
                room.getStatus(),
                "updated room",
                new WaitingRoomUpdateResponseDto(room.getId(), room.getUsers())
        );
        room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", updatedRoomDto));

        if (room.getUsers().stream().allMatch(userRedis -> userRedis.getStatus() == UserRedis.WaitingPlayerStatusEnum.READY))
            this.eventPublisher.publishEvent(new StartPlayingEvent(this, room.getId()));
    }

    @EventListener
    private void startPlayingEventHandler(StartPlayingEvent event) {
        Room room = this.roomManagerService.startGame(event.getRoomId());

        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "All players are ready. Starting the game...",
                new StartingMatchResponseDto("pizza", room.getStartTimePlaying(), room.getEndTimePlaying())
        );
        room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", responseDto));
    }

    @MessageMapping("/rooms/send_draw")
    public void receiveUsersDraw(@Payload byte[] payload, Principal principal) {
        System.out.println("receiving user draw: " + payload.length);
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.setUserDraw(user, payload);

        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "succesfully receive user draw"
        );
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @EventListener
    private void handleStartingVotingForNextDrawingEvent(StartingVotingForNextDrawingEvent event) {
        Room room = event.getRoom();
        room = this.roomManagerService.startVotingForNextDrawing(room.getId());
        String targetUsername = room.getUsers().get(room.getCurrentVotingIndex()).getUsername();
        byte[] drawSvg = room.getUsers().get(room.getCurrentVotingIndex()).getSvg();

        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "Vote to " + targetUsername,
                new VotingResponseDto(drawSvg, targetUsername, room.getTheme(), room.getStartTimeVoting(), room.getEndTimeVoting())
        );
        room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", responseDto));
    }

    @MessageMapping("/rooms/send_vote")
    public void receiveVotes(VoteRequestDto requestDto, Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        UUID roomId = this.roomPersistenceService.getRoomIdOfUser(user.getId());
        Room room = this.roomManagerService.doVote(roomId, user.getUsername(), requestDto.getRate());

        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "succesfully received vote"
        );
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);
    }

    @EventListener
    private void handleStartResultEvent(StartResultEvent event) {
        Room room = this.roomManagerService.startResult(event.getRoom().getId());

        List<UserRatesDto> userRates = new ArrayList<>();
        room.getUsers().forEach(user -> {
            UserRatesDto userRate = new UserRatesDto(user.getUsername(), user.getVoteResult());
            userRates.add(userRate);
        });
        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "result of game",
                new ResultResponseDto(room.getTheme(), userRates)
        );
        room.getUsers().forEach(u -> this.template.convertAndSendToUser(u.getUsername(), "/queue/reply", responseDto));
    }
}
