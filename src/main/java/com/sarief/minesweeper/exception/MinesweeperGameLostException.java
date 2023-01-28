package com.sarief.minesweeper.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinesweeperGameLostException extends MinesweeperException {
    public MinesweeperGameLostException() {
        super("Game Lost", "error.code.game.state.lost");

    }
}
