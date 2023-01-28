package com.sarief.minesweeper.rest;

import com.sarief.minesweeper.dto.ExceptionResponse;
import com.sarief.minesweeper.exception.MinesweeperException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(WebRequest request, Exception e) {
        log.error("Exception: " + e.getMessage(), e);
        ExceptionResponse response = ExceptionResponse.builder().errorMessage(e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({MinesweeperException.class})
    public ResponseEntity<ExceptionResponse> handleException(WebRequest request, MinesweeperException e) {
        log.error("Exception: " + e.getMessage(), e);
        ExceptionResponse response = ExceptionResponse.builder()
                .errorMessage(e.getMessage())
                .errorCode(e.getCode())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
