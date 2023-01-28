package com.sarief.minesweeper.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Reset game Request
 */
@Getter
@Setter
public class ResetGameRequest {
    private int height;
    private int width;
    private int mineCount;
    Long seed;
}
