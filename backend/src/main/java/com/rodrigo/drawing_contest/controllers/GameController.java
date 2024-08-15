package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.websockets.WebSocketDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.CreatePrivateRoomRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.EnterInPrivateRoomRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.FinalDrawRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.request.VoteRequestDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.CreateRoomResponseDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.EnterInPrivateRoomResponseDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.StartingMatchResponseDto;
import com.rodrigo.drawing_contest.dtos.websockets.response.WaitingRoomUpdateResponseDto;
import com.rodrigo.drawing_contest.models.room.Room;
import com.rodrigo.drawing_contest.models.user.User;
import com.rodrigo.drawing_contest.models.user.UserRedis;
import com.rodrigo.drawing_contest.services.RoomManagerService;
import com.rodrigo.drawing_contest.services.RoomPersistenceService;
import com.rodrigo.drawing_contest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class GameController {

    private final SimpMessagingTemplate template;
    private final RoomPersistenceService roomPersistenceService;
    private final RoomManagerService roomManagerService;
    private final UserService userService;

    @MessageMapping("/rooms/private/create")
    public void createPrivateRoom(CreatePrivateRoomRequestDto requestDto, Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.createPrivateRoom(user, requestDto.getRoomPassword());

        WebSocketDto<CreateRoomResponseDto> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "room {" + room.getId() + "} succesfully created",
                new CreateRoomResponseDto(room.getId())
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
                new WaitingRoomUpdateResponseDto(room.getUsers())
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

        if (room != null) {
            WebSocketDto<WaitingRoomUpdateResponseDto> updatedRoomDto = new WebSocketDto<>(
                    room.getStatus(),
                    "updated room",
                    new WaitingRoomUpdateResponseDto(room.getUsers())
            );
            for (UserRedis userRedis : room.getUsers())
                this.template.convertAndSendToUser(userRedis.getUsername(), "/queue/reply", updatedRoomDto);
        }
    }

    @MessageMapping("/rooms/user_status")
    public void changeUserStatus(Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.changeUserStatus(user);

        WebSocketDto<?> responseDto = new WebSocketDto<>(room.getStatus(), "succesfully set player status to READY");
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);

        WebSocketDto<WaitingRoomUpdateResponseDto> updatedRoomDto = new WebSocketDto<>(
                room.getStatus(),
                "updated room",
                new WaitingRoomUpdateResponseDto(room.getUsers())
        );
        for (UserRedis userRedis : room.getUsers())
            this.template.convertAndSendToUser(userRedis.getUsername(), "/queue/reply", updatedRoomDto);

        if (room.getUsers().stream().allMatch(userRedis -> userRedis.getStatus() == UserRedis.WaitingPlayerStatusEnum.READY))
            this.startGame(room);
    }

    private void startGame(Room room) {
        room = this.roomManagerService.startGame(room.getId());

        System.out.println("starting game at " + room.getStartTime().toString() + " with end at " + room.getEndTime().toString());

        WebSocketDto<?> allReadyDto = new WebSocketDto<>(
                room.getStatus(),
                "All players are ready. Starting the game...",
                new StartingMatchResponseDto("pizza", room.getStartTime(), room.getEndTime())
        );
        for (UserRedis userRedis : room.getUsers())
            template.convertAndSendToUser(userRedis.getUsername(), "/queue/reply", allReadyDto);
    }

    @MessageMapping("/rooms/send_draw")
    public void receiveUsersDraw(FinalDrawRequestDto requestDto, Principal principal) {
        String username = principal.getName();
        User user = this.userService.findUserByUsername(username);
        Room room = this.roomManagerService.setUserDraw(user, requestDto.getDraw());

        WebSocketDto<?> responseDto = new WebSocketDto<>(
                room.getStatus(),
                "succesfully receive user draw"
        );
        this.template.convertAndSendToUser(username, "/queue/reply", responseDto);

        if (room.getUsers().stream().allMatch(userRedis -> userRedis.getSvg() != null)) {
            WebSocketDto<?> allReadyDto = new WebSocketDto<>(
                    room.getStatus(),
                    "received all draws. now it is time to vote"
            );
            for (UserRedis j : room.getUsers())
                template.convertAndSendToUser(j.getUsername(), "/queue/reply", allReadyDto);
        }

//        // time to vote
//        if (room.getUsers().stream().allMatch(userRedis -> userRedis.getSvg() != null)) {
//            room = this.roomManagerService.startVoting(room.getId());
//            System.out.println("TIME TO VOTE in room {" + room.getId() + "}");
//
//            for (UserRedis i : room.getUsers()) {
//                WebSocketDto<?> allReadyDto = new WebSocketDto<>(
//                        room.getStatus(),
//                        "Vote to user x",
//                        new StartingMatchResponseDto("pizza", room.getStartTime(), room.getEndTime())
//                );
//                for (UserRedis j : room.getUsers())
//                    template.convertAndSendToUser(j.getUsername(), "/queue/reply", allReadyDto);
//
//                // wait to send their draw
//            }
//        }
    }
}
