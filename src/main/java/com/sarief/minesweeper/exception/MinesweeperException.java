package com.sarief.minesweeper.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MinesweeperException extends RuntimeException{
    private String message;
    private String code; // if needed for translation
}
