package com.sarief.minesweeper.entity;


import com.sarief.minesweeper.exception.MinesweeperException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Squared implementation of {@link MinefieldMap}
 */
@Getter
@Setter
public class MinefieldSquaredMap implements MinefieldMap {
    private static final int NUMBER_OF_DIRECTIONS_TO_ADJACENT_CELLS = 8;
    private static final int MINIMUM_NUMBER_OF_MINELESS_CELLS = 9;
    private static final int MINIMUM_NUMBER_OF_MINES = 1;
    private static final int MINIMUM_ALLOWED_HEIGHT = 5;
    private static final int MINIMUM_ALLOWED_WIDTH = 5;
    private GameState gameState = GameState.INITIAL;
    private Map<MineCellCoordinates, MineCell> minefield = new HashMap<>();

    private Long seed;

    private int openedCells;

    private int minelessCells;
    private int mineCount;
    private int height;
    private int width;

    private int flagsUsed;

    private Long startingTimeInMillis;

    @Override
    public boolean isGameLost() {
        return gameState == GameState.FINISHED_LOST;
    }

    @Override
    public void finishGameLost() {
        gameState = GameState.FINISHED_LOST;
    }

    public MineCell findByCoordinates(MineCellCoordinates mineCellCoordinate) {
        return minefield.get(mineCellCoordinate);
    }

    @Override
    public List<MineCell> getAdjacentCells(MineCellCoordinates mineCellCoordinates) {

        MineCell minecell = findByCoordinates(mineCellCoordinates);
        MineCellAdjacentInfo cachedAdjacentInfo = minecell.getMineCellAdjacentInfo();
        if (cachedAdjacentInfo != null && cachedAdjacentInfo.getAdjacentCells() != null) {
            return cachedAdjacentInfo.getAdjacentCells();
        }

        List<MineCell> pointersToAdjacentCells = findAllAdjacentCells(mineCellCoordinates);

        // there is no need to search for the cells multiple times, so let's just cache
        MineCellAdjacentInfo adjacentInfo = MineCellAdjacentInfo.builder().adjacentCells(pointersToAdjacentCells).build();
        minecell.setMineCellAdjacentInfo(adjacentInfo);

        return pointersToAdjacentCells;
    }

    private List<MineCell> findAllAdjacentCells(MineCellCoordinates mineCellCoordinates) {
        List<MineCell> pointersToAdjacentCells = new ArrayList<>(NUMBER_OF_DIRECTIONS_TO_ADJACENT_CELLS);

        // Squared field has 8 directions that can possibly have adjacent cells
        // 1 2 3
        // 4 * 5
        // 6 7 8
        // if asterisk is a point of origin for direction.
        // if we only need to find closest adjacent ones on grid, relative to point of origin,
        // their coordinates need to be adjusted by 1 or -1 depending on direction
        // which should look something like this:
        // (-1;-1) (0;-1) (1;-1)
        // (-1; 0) (skip) (1; 0)
        // (-1; 1) (0; 1) (1; 1)
        // In other words we need to traverse 3x3 grid, but skip point of origin as it is not adjacent

        for (int yAdjustment = -1; yAdjustment <= 1; yAdjustment++) {
            for (int xAdjustment = -1; xAdjustment <= 1; xAdjustment++) {
                if (yAdjustment == 0 && xAdjustment == 0) {
                    continue; // skip point-of-origin cell
                }

                MineCellCoordinates adjacentCoordinates = getAdjustedCoordinate(mineCellCoordinates, xAdjustment, yAdjustment);
                MineCell adjacentCell = findByCoordinates(adjacentCoordinates);
                if (adjacentCell != null) { // if no element given direction found
                    pointersToAdjacentCells.add(adjacentCell);
                }
            }
        }

        return pointersToAdjacentCells;
    }


    private static MineCellCoordinates getAdjustedCoordinate(MineCellCoordinates mineCellCoordinates,
                                                             int xAdjustment, int yAdjustment) {
        return MineCellCoordinates.builder()
                .xCoordinate(mineCellCoordinates.getXCoordinate() + xAdjustment)
                .yCoordinate(mineCellCoordinates.getYCoordinate() + yAdjustment)
                .build();
    }

    public MineCell findByCoordinates(int x, int y) {
        return findByCoordinates(MineCellCoordinates.builder()
                .xCoordinate(x)
                .yCoordinate(y)
                .build());
    }

    @Override
    public int getNumberOfAdjecentMines(MineCell selectedMineCell) {
        MineCellAdjacentInfo cachedAdjacentInfo = selectedMineCell.getMineCellAdjacentInfo();
        if (cachedAdjacentInfo != null && cachedAdjacentInfo.getNumberOfAdjacentMines() != null) {
            return cachedAdjacentInfo.getNumberOfAdjacentMines();
        }

        // getAdjacentCell should create cached MineCellAdjacentInfo instance,
        // so afterwards only numberOfAdjacentMines needs to be populated
        List<MineCell> adjacentCells = getAdjacentCells(selectedMineCell.getCoordinates());

        int mineCounter = 0;

        for (MineCell adjacentCell : adjacentCells) {
            if (adjacentCell.containsMine()) {
                mineCounter++;
            }
        }

        // if we didn't have cache instance before we started, we will have after method getAdjacentCells
        // but we still need to get it if we want to set numberOfAdjacentMines
        if (cachedAdjacentInfo == null) {
            cachedAdjacentInfo = selectedMineCell.getMineCellAdjacentInfo();
        }

        cachedAdjacentInfo.setNumberOfAdjacentMines(mineCounter);

        return mineCounter;
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void initiate(int xStart, int yStart) {

        if (gameState != GameState.PREPARED) {
            throw new MinesweeperException("Game already initiated", "error.code.game.state.lost");
        }

        this.startingTimeInMillis = System.currentTimeMillis();

        Random rand = new Random(this.seed);

        int currentMineCount = 0;
        while (currentMineCount < mineCount) {
            int yCoordinate = rand.nextInt(height);
            int xCoordinate = rand.nextInt(width);

            if (xCoordinate == xStart && yCoordinate == yStart) {
                continue; // skip if this is starting point, starting point always mineless
                // we trust that number of mines < number of fields due to previous checks
            }

            MineCell mineCell = minefield.get(MineCellCoordinates.builder()
                    .xCoordinate(xCoordinate)
                    .yCoordinate(yCoordinate).build());

            if (!mineCell.containsMine()) {
                mineCell.setContainsMine(true);
                currentMineCount++;
            }
        }

        this.gameState = GameState.STARTED;

    }

    public void reset(int height, int width, int mineCount, Long seed) {

        checkInitialParametersValid(height, width, mineCount);

        minefield = new HashMap<>();
        this.height = height;
        this.width = width;
        this.mineCount = mineCount;
        this.minelessCells = height * width - mineCount;

        this.seed = seed != null ? seed : System.currentTimeMillis();

        // We don't have to do this, but this way it's much easier to keep track of what is going on and place mines
        // rather than having separate map for mines or initiating Cells while calculating adjacent cells
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                MineCellCoordinates coordinates = MineCellCoordinates.builder().xCoordinate(x).yCoordinate(y).build();
                MineCell mineCell = MineCell
                        .builder()
                        .coordinates(coordinates)
                        .open(false)
                        .marked(false)
                        .build();

                minefield.put(coordinates, mineCell);
            }
        }

        gameState = GameState.PREPARED;
    }

    @Override
    public void flagCell(int x, int y) {
        MineCell mineCell = findByCoordinates(x, y);
        if (!mineCell.isOpen() && !mineCell.isMarked()) {
            mineCell.setMarked(true);
            // concurrency can be a problem, but for the purposes of simplifying we assume there is only one player
            // that only makes requests after previous requests finished...
            flagsUsed++;
        }

    }

    @Override
    public void unflagCell(int x, int y) {
        MineCell byCoordinates = findByCoordinates(x, y);
        if (byCoordinates.isMarked()) {
            byCoordinates.setMarked(false);
            // concurrency can be a problem, but for the purposes of simplifying we assume there is only one player
            // that only makes requests after previous requests finished...
            flagsUsed--;
        }
    }

    @Override
    public void addToCountOfOpenCells() {
        openedCells++;

        if (openedCells == minelessCells) {
            gameState = GameState.FINISHED_WON;
        }
    }

    @Override
    public boolean isGameWon() {
        return gameState == GameState.FINISHED_WON;
    }

    @Override
    public char[][] getField() {
        char[][] field = new char[height][width];
        for (int y = 0; y < height; y++) {
            char[] marks = new char[width];
            for (int x = 0; x < width; x++) {
                MineCell minecell = findByCoordinates(x, y);
                if (minecell.isMarked()) {
                    marks[x] = '⚑';
                    continue;

                }
                if (minecell.isOpen() && minecell.containsMine()) {
                    marks[x] = '*';
                    continue;
                }
                MineCellAdjacentInfo adjacentInfo = minecell.getMineCellAdjacentInfo();
                Integer numberOfAdjacentMines = adjacentInfo != null ? adjacentInfo.getNumberOfAdjacentMines() : null;
                char mark = numberOfAdjacentMines != null ?
                        Character.forDigit(numberOfAdjacentMines, 10) : '?';
                marks[x] = mark;
            }
            field[y] = marks;
        }
        return field;
    }

    private static void checkInitialParametersValid(int height, int width, int mineCount) {
        if (height < MINIMUM_ALLOWED_HEIGHT) {
            throw new MinesweeperException("Height less than allowed: " + MINIMUM_ALLOWED_HEIGHT,
                    "error.code.limit.height");
        }

        if (width < MINIMUM_ALLOWED_WIDTH) {
            throw new MinesweeperException("Width less than allowed: " + MINIMUM_ALLOWED_WIDTH,
                    "error.code.limit.height");
        }

        // Original has bug where you actually can add more mines than fields if you type the number
        // (there is counter that has up and down arrows, there check works, the typing is the problem)
        if (mineCount < MINIMUM_NUMBER_OF_MINES) {
            throw new MinesweeperException("Less mines than allowed. " +
                    "Allowed minimum of mines: " + MINIMUM_NUMBER_OF_MINES, "error.code.limit.height");
        }

        int maximumAllowedNumberOfMines = height * width - MINIMUM_NUMBER_OF_MINELESS_CELLS;
        if (mineCount > maximumAllowedNumberOfMines) {
            throw new MinesweeperException("More mines than allowed. " +
                    "Allowed maximum of mines for this field: " + maximumAllowedNumberOfMines
                    , "error.code.limit.height");
        }
    }

}
