package com.rodrigo.drawing_contest.controllers;

import com.rodrigo.drawing_contest.dtos.http.response.RestErrorDto;
import com.rodrigo.drawing_contest.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<RestErrorDto> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e,
            BindingResult bindingResult
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.UNPROCESSABLE_ENTITY, "invalid fields", bindingResult);
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    private ResponseEntity<RestErrorDto> invalidJwtTokenExceptionHandler(
            InvalidJwtTokenException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<RestErrorDto> runtimeExceptionHandler(
            RuntimeException e
    ) {
        log.info(String.valueOf(e.getCause()) + " | " + e.getMessage() + " | " + e.getClass());

        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<RestErrorDto> badCredentialsExceptionHandler(
            BadCredentialsException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.UNAUTHORIZED, "bad credentials");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<RestErrorDto> entityNotFoundExceptionHandler(
            EntityNotFoundException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(UsernameAlreadyUsedException.class)
    private ResponseEntity<RestErrorDto> usernameAlreadyUsedExceptionHandler(
            UsernameAlreadyUsedException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(UserPasswordDoNotMatchException.class)
    private ResponseEntity<RestErrorDto> userPasswordDoNotMatchExceptionHandler(
            UserPasswordDoNotMatchException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(UserIsNotInAnyRoomException.class)
    private ResponseEntity<RestErrorDto> userIsNotInAnyRoomExceptionHandler(
            UserIsNotInAnyRoomException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.FORBIDDEN, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(ActionDoNotMatchWithRoomStatusException.class)
    private ResponseEntity<RestErrorDto> actionDoNotMatchWithRoomStatusExceptionHandler(
            ActionDoNotMatchWithRoomStatusException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }

    @ExceptionHandler(UserIsNotInThisRoomException.class)
    private ResponseEntity<RestErrorDto> userIsNotInThisRoomExceptionHandler(
            UserIsNotInThisRoomException e
    ) {
        RestErrorDto restErrorDto = new RestErrorDto(HttpStatus.FORBIDDEN, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(restErrorDto);
    }
}
