package com.sarief.minesweeper.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExceptionResponse {
    private String errorMessage;
    private String errorCode;
}
