package com.sarief.minesweeper.entity;


import com.sarief.minesweeper.exception.MinesweeperNotImplementedException;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Hexagonal implementation of {@link MinefieldMap}
 */
// TODO: implement. This is just stub for hexagonal map
@Getter
@Setter
public class MinefieldHexagonalMap implements MinefieldMap {

    private static final int NUMBER_OF_DIRECTIONS_TO_ADJACENT_CELLS = 6;
    private Map<MineCellCoordinates, MineCell> minefield;
    private GameState gameState;

    @Override
    public boolean isGameLost() {
        return gameState == GameState.FINISHED_WON || gameState == GameState.FINISHED_LOST;
    }

    @Override
    public void finishGameLost() {
        gameState = GameState.FINISHED_LOST;
    }

    public MineCell findByCoordinates(MineCellCoordinates mineCellCoordinate) {
        return minefield.get(mineCellCoordinate);
    }

    @Override
    public List<MineCell> getAdjacentCells(MineCellCoordinates mineCellCoordinate) {
        throw new MinesweeperNotImplementedException();
    }

    public MineCell findByCoordinates(int x, int y) {
        return findByCoordinates(MineCellCoordinates.builder().xCoordinate(x).yCoordinate(y).build());
    }

    @Override
    public int getNumberOfAdjecentMines(MineCell selectedMineCell) {
        throw new MinesweeperNotImplementedException();
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void initiate(int xStart, int yStart) {
        throw new MinesweeperNotImplementedException();
    }

    @Override
    public void reset(int height, int width, int minecount, Long seed) {
        throw new MinesweeperNotImplementedException();
    }

    @Override
    public void flagCell(int x, int y) {
        throw new MinesweeperNotImplementedException();
    }

    @Override
    public void unflagCell(int x, int y) {
        throw new MinesweeperNotImplementedException();

    }

    @Override
    public void addToCountOfOpenCells() {
        throw new MinesweeperNotImplementedException();
    }

    @Override
    public boolean isGameWon() {
        return gameState == GameState.FINISHED_WON;
    }

    @Override
    public char[][] getField() {
        throw new MinesweeperNotImplementedException();
    }


}
