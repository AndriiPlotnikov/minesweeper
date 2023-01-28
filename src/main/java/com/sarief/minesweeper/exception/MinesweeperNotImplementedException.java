package com.sarief.minesweeper.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MinesweeperNotImplementedException extends MinesweeperException {

    public MinesweeperNotImplementedException() {
        super("Not Implemented Yet", "error.code.implemented.not");

    }
}
