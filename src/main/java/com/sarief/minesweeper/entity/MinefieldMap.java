package com.sarief.minesweeper.entity;


import java.util.List;

/**
 * Interface for Minefield maps. Map store state of the current game
 *
 * Minefield as map is chosen for ease of finding specific mine and not needing to traverse each cell
 * that would be required in other structures. Except for 2d array, but that is if in the future it will never be
 * extended to 3d array or anything else. It's much simpler to write it through map interface
 */
public interface MinefieldMap {

    /**
     * Check if game already finished and is Lost ({@link GameState} FINISHED_LOST)
     *
     * @return true if lost. false otherwise
     */
    boolean isGameLost();

    /**
     * Check if game already finished and is Won ({@link GameState} FINISHED_WON)
     *
     * @return true if won. false otherwise
     */
    boolean isGameWon();

    /**
     * Change game state to lost. ({@link GameState} FINISHED_LOST)
     */
    void finishGameLost();

    /**
     * Get specific MineCell data by coordinates
     *
     * @param mineCellCoordinate coordinates data
     * @return seeked minecell
     */
    MineCell findByCoordinates(MineCellCoordinates mineCellCoordinate);

    /**
     * Find adjacent cells that are neighbors of specified cell. Number of neighbors depends on Map.
     *
     * @param mineCellCoordinate coordinates of specified cell
     * @return neighbor cells
     */
    List<MineCell> getAdjacentCells(MineCellCoordinates mineCellCoordinate);

    /**
     * Find specific cell by coordinates
     *
     * @param x - x coordinate
     * @param y - y coordinate
     * @return found cell or null if not found
     */
    // if this ever needs to be extended to 3d+ board, methods below will not work
    // but I'm going to leave them for now, since they make code more readable and I'm not creating 3d+ minesweeper
    MineCell findByCoordinates(int x, int y);

    /**
     * Find number of adjacent mines to specified cell
     *
     * @param selectedMineCell specified cell
     * @return number of adjacent mines. 0 means no mines
     */
    int getNumberOfAdjecentMines(MineCell selectedMineCell);

    /**
     * Get current state of the game
     *
     * @return current state of the game
     */
    GameState getGameState();

    /**
     * Start game from specific point. Must be called before first turn.
     * In minesweeper first turn is always mineless
     *
     * @param xStart x coordinate
     * @param yStart y coordinate
     */
    void initiate(int xStart, int yStart);

    /**
     * Set or reset game state with specific parameters
     *
     * @param height - number of rows
     * @param width - number of columns
     * @param minecount - number of mines
     * @param seed - specific seed used for mine placement algorithm. Passing null will make it random.
     */
    void reset(int height, int width, int minecount, Long seed);


    /**
     * flag cell to avoid accidentally opening it
     *
     * @param x - x coordinate
     * @param y - y coordinate
     */
    void flagCell(int x, int y);

    /**
     * remove flag from flagged cell
     *
     * @param x - x coordinate
     * @param y - y coordinate
     */
    void unflagCell(int x, int y);

    /**
     * add one to count of open cells
     */
    void addToCountOfOpenCells();


    /**
     * Get map in form of double array of single character marks
     *
     * @return map in form of double array of single character marks
     */
    char[][] getField();

}
