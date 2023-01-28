package com.sarief.minesweeper.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Request with specific coordinates
 */
@Getter
@Setter
public class CellCoordinateRequest {

    private int x;
    private int y;
}
