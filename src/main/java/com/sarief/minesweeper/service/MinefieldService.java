package com.sarief.minesweeper.service;

import com.sarief.minesweeper.dto.GameStatusResponse;
import com.sarief.minesweeper.entity.GameState;
import com.sarief.minesweeper.entity.MineCell;
import com.sarief.minesweeper.entity.MinefieldMap;
import com.sarief.minesweeper.entity.MinefieldSquaredMap;
import com.sarief.minesweeper.exception.MinesweeperGameLostException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for logic that works with minefield
 */
@Service
public class MinefieldService {

    // normally each user would have his own map on backend, but we'll compromise with this since this is not going prod
    // if needs to be moved to prod: add user handling + store gamestate in redis or, worst case, postgres
    private MinefieldMap currentMinefield;


    /**
     * Open cell by specific coordinates. Game lost if cell contains Mine.
     * Will open nearby cells if there are no mine nearby (recursive)
     *
     * @param x - x coordinate
     * @param y - y coordinate
     * @return current game status
     */
    public GameStatusResponse openCell(int x, int y) {
        if (currentMinefield.isGameLost()) {
            throw new MinesweeperGameLostException();
        }

        GameState currentGameState = currentMinefield.getGameState();
        if (currentGameState == GameState.PREPARED) {
            // initiate field, skip x,y point
            currentMinefield.initiate(x, y);
        }

        MineCell selectedMineCell = currentMinefield.findByCoordinates(x, y);

        if (selectedMineCell.isMarked()) {
            throw new UnsupportedOperationException("Cannot open flagged cell");
        }

        openCell(selectedMineCell);

        return getGameStatus();

//        currentMinefield.checkGameStatus();
    }

    private void openCell(MineCell selectedMineCell) {
        if (selectedMineCell.isOpen()) {
            return;
        }

        if (currentMinefield.isGameWon()) {
            return;
        }

        if (currentMinefield.isGameLost()) {
            throw new MinesweeperGameLostException();
        }

        if (selectedMineCell.isMarked()) { // remove flag if opened from nearby cell
            selectedMineCell.setMarked(false);
        }

        // step 1: check if it has mines
        if(selectedMineCell.containsMine()){
            selectedMineCell.setOpen(true);
            currentMinefield.finishGameLost();
            return;
        }

        // step 2: no mine = open cell
        selectedMineCell.setOpen(true);
        currentMinefield.addToCountOfOpenCells();

        // step 3: if there are no adjacent mines - open all adjacent cells;
        if (currentMinefield.getNumberOfAdjecentMines(selectedMineCell) == 0) {
            openAdjacentCells(selectedMineCell);
        }
    }

    private void openAdjacentCells(MineCell selectedMineCell) {
        List<MineCell> adjacentCells = currentMinefield.getAdjacentCells(selectedMineCell.getCoordinates());

        for (MineCell adjacentCell : adjacentCells) {
            openCell(adjacentCell);
        }
    }


    /**
     * reset game state with specified parameters
     *
     * @param height number of rows
     * @param width number of columns
     * @param mineCount mine count
     * @param seed seed for mine placing algorithm. null for random
     */
    public void reset(int height, int width, int mineCount, Long seed) {
        currentMinefield = new MinefieldSquaredMap();

        currentMinefield.reset(height, width, mineCount, seed);
    }

    /**
     * get current game status
     *
     * @return current game status
     */
    public GameStatusResponse getGameStatus() {
        return GameStatusResponse.builder()
                .gameState(currentMinefield.getGameState())
                .field(toDisplayField(currentMinefield.getField()))
                .build();
    }

    private String[] toDisplayField(char[][] field) {
        String[] stringField = new String[field.length];
        for (int i = 0; i < field.length; i++) {
            stringField[i] = StringUtils.join(field[i], ' ');
        }
        return stringField;
    }

    /**
     * Flag cell in specified coordinates
     *
     * @param x - x coordinate
     * @param y - y coordinate
     * @return current game status
     */
    public GameStatusResponse flagCell(int x, int y) {
        currentMinefield.flagCell(x, y);
        return getGameStatus();
    }

    /**
     * Remove flag from cell in specified coordinates
     *
     * @param x - x coordinate
     * @param y - y coordinate
     * @return current game status
     */
    public GameStatusResponse unflagCell(int x, int y) {
        currentMinefield.unflagCell(x, y);
        return getGameStatus();
    }
}
