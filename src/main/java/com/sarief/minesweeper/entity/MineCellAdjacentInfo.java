package com.sarief.minesweeper.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Info on adjacent mine cells
 */
@Getter
@Setter
@Builder
public class MineCellAdjacentInfo {

    private List<MineCell> adjacentCells;
    private Integer numberOfAdjacentMines;
}
