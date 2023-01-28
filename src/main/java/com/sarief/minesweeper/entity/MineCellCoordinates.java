package com.sarief.minesweeper.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Coordinates
 */
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class MineCellCoordinates {
    private int xCoordinate;
    private int yCoordinate;
}
