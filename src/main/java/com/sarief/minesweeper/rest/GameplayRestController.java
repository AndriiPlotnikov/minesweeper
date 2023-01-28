package com.sarief.minesweeper.rest;

import com.sarief.minesweeper.dto.CellCoordinateRequest;
import com.sarief.minesweeper.dto.GameStatusResponse;
import com.sarief.minesweeper.dto.ResetGameRequest;
import com.sarief.minesweeper.service.MinefieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for minesweeper
 */
@RequestMapping(value = "/api/v1/minesweeper", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class GameplayRestController {
    @Autowired
    private MinefieldService minefieldService;

    /**
     * Set or reset game state
     *
     * @param resetGameRequest parameters
     */
    @RequestMapping(value = "/resetGame", method = RequestMethod.POST)
    public void resetGame(@RequestBody @Validated ResetGameRequest resetGameRequest) {
        minefieldService.reset(resetGameRequest.getHeight(), resetGameRequest.getWidth(),
                resetGameRequest.getMineCount(), resetGameRequest.getSeed());
    }

    /**
     * Get current game status
     *
     * @return current game status
     */
    @RequestMapping(value = "/gameStatus", method = RequestMethod.GET)
    public GameStatusResponse getGameStatus() {
        return minefieldService.getGameStatus();
    }

    /**
     * Open cell in specific coordinates. Flagged cells cannot be open
     *
     * @param request cell coordinates
     * @return current game state
     */
    @RequestMapping(value = "/openCell", method = RequestMethod.POST)
    public GameStatusResponse openCell(@RequestBody @Validated CellCoordinateRequest request) {
        return minefieldService.openCell(request.getX(), request.getY());
    }

    /**
     * Flag cell by specific coordinates. Flagged cells cannot be open
     *
     * @param request cell coordinates
     * @return current game state
     */
    @RequestMapping(value = "/flagCell", method = RequestMethod.POST)
    public GameStatusResponse flagCell(@RequestBody @Validated CellCoordinateRequest request) {
        return minefieldService.flagCell(request.getX(), request.getY());
    }

    /**
     * Unflag flagged cell by specific coordinates. Flagged cells cannot be open
     *
     * @param request cell coordinates
     * @return current game state
     */
    @RequestMapping(value = "/unflagCell", method = RequestMethod.POST)
    public GameStatusResponse unflagCell(@RequestBody @Validated CellCoordinateRequest request) {
        return minefieldService.unflagCell(request.getX(), request.getY());
    }


}
