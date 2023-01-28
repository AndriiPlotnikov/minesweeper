package com.sarief.minesweeper.rest;

import com.sarief.minesweeper.dto.CellCoordinateRequest;
import com.sarief.minesweeper.dto.ResetGameRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameplayRestControllerTest {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getGameStatus_gameNotSet_internalServerError() {
        String url = "http://localhost:" + port + "/api/v1/minesweeper/gameStatus";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void getGameStatus_gameSet_resultOk() {
        setGame(123L);

        String gameStatusUrl = "http://localhost:" + port + "/api/v1/minesweeper/gameStatus";
        ResponseEntity<String> gameStatusResponse = restTemplate.getForEntity(gameStatusUrl, String.class);
        assertThat(gameStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void openCell_gameSet_resultOk() {
        setGame(null); // null seed == random seed

        CellCoordinateRequest resetGameRequest = new CellCoordinateRequest();
        resetGameRequest.setX(1);
        resetGameRequest.setY(1);

        String openCellUrl = "http://localhost:" + port + "/api/v1/minesweeper/openCell";
        ResponseEntity<String> openCellResponse = restTemplate.postForEntity(openCellUrl, resetGameRequest, String.class);

        assertThat(openCellResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(openCellResponse.getBody()).isNotNull();
        assertThat(openCellResponse.getBody().contains("STARTED")).isTrue();
    }

    @Test
    public void flagCell_gameSet_flagSet() {
        setGame(null); // null seed == random seed

        CellCoordinateRequest resetGameRequest = new CellCoordinateRequest();
        resetGameRequest.setX(3);
        resetGameRequest.setY(2);

        String flagCellUrl = "http://localhost:" + port + "/api/v1/minesweeper/flagCell";
        ResponseEntity<String> flagCellResponse = restTemplate.postForEntity(flagCellUrl, resetGameRequest, String.class);

        assertThat(flagCellResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(flagCellResponse.getBody()).isNotNull();
        assertThat(flagCellResponse.getBody().contains("PREPARED")).isTrue();
        assertThat(flagCellResponse.getBody().contains("⚑")).isTrue();
    }

    @Test
    public void unflagCell_gameSet_flagSetAndUnset() {
        setGame(null); // null seed == random seed

        CellCoordinateRequest resetGameRequest = new CellCoordinateRequest();
        resetGameRequest.setX(3);
        resetGameRequest.setY(2);

        String flagUrl = "http://localhost:" + port + "/api/v1/minesweeper/flagCell";
        ResponseEntity<String> flagCellResponse = restTemplate.postForEntity(flagUrl, resetGameRequest, String.class);

        assertThat(flagCellResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(flagCellResponse.getBody()).isNotNull();
        assertThat(flagCellResponse.getBody().contains("PREPARED")).isTrue();
        assertThat(flagCellResponse.getBody().contains("⚑")).isTrue();

        String unflagUrl = "http://localhost:" + port + "/api/v1/minesweeper/unflagCell";
        ResponseEntity<String> unflagCellResponse = restTemplate.postForEntity(unflagUrl, resetGameRequest, String.class);

        assertThat(unflagCellResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(unflagCellResponse.getBody()).isNotNull();
        assertThat(unflagCellResponse.getBody().contains("PREPARED")).isTrue();
        assertThat(unflagCellResponse.getBody().contains("⚑")).isFalse();
    }


    @Test
    public void playGameToWin_seedAndStepsPredefined_resultWin() {
        Long predefinedSeed = 15625765L;
        setGame(predefinedSeed);

        String openCellUrl = "http://localhost:" + port + "/api/v1/minesweeper/openCell";

        CellCoordinateRequest firstTurnRequest = new CellCoordinateRequest();
        firstTurnRequest.setX(1);
        firstTurnRequest.setY(1);
        ResponseEntity<String> openCellResponse = restTemplate.postForEntity(openCellUrl, firstTurnRequest, String.class);

        assertThat(openCellResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(openCellResponse.getBody()).isNotNull();
        assertThat(openCellResponse.getBody().contains("STARTED")).isTrue();

        openCell(0, 0);
        openCell(4, 4);
        openCell(4, 0);
        openCell(0, 4);
        openCell(4, 1);
        openCell(2, 4);
        openCell(3, 2);
        openCell(3, 1);


        String gameStatusUrl = "http://localhost:" + port + "/api/v1/minesweeper/gameStatus";
        ResponseEntity<String> gameStatusResponse = restTemplate.getForEntity(gameStatusUrl, String.class);
        assertThat(gameStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(gameStatusResponse.getBody().contains("FINISHED_WON")).isTrue();

    }

    private void openCell(int x, int y) {
        String openCellUrl = "http://localhost:" + port + "/api/v1/minesweeper/openCell";

        CellCoordinateRequest secondTurnRequest = new CellCoordinateRequest();
        secondTurnRequest.setX(x);
        secondTurnRequest.setY(y);
        restTemplate.postForEntity(openCellUrl, secondTurnRequest, String.class);
    }


    private void setGame(Long seed) {
        ResetGameRequest resetGameRequest = new ResetGameRequest();
        resetGameRequest.setHeight(5);
        resetGameRequest.setWidth(5);
        resetGameRequest.setMineCount(5);
        resetGameRequest.setSeed(seed);

        String url = "http://localhost:" + port + "/api/v1/minesweeper/resetGame";
        ResponseEntity<String> response = restTemplate.postForEntity(url, resetGameRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
