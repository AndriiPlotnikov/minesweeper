package com.sarief.minesweeper.dto;

import com.sarief.minesweeper.entity.GameState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response that contains game status
 */
@Getter
@Setter
@Builder
public class GameStatusResponse {

    private GameState gameState;
    private String[] field;
}
