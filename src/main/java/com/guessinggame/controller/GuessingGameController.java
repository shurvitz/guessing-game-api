package com.guessinggame.controller;

import com.guessinggame.api.GuessingGameApi;
import com.guessinggame.component.Game;
import com.guessinggame.component.GameRange;
import com.guessinggame.component.GameReply;
import com.guessinggame.component.GameRepository;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the Guessing Game API
 */
@RestController
@RequestMapping("/guess")
public class GuessingGameController {
    private final GameRepository gameRepository;
    private final GuessingGameApi guessingGameApi;

    public GuessingGameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        guessingGameApi = new GuessingGameApi(this.gameRepository);
    }

    /**
     * Process POST request for starting a new game (e.g. http://localhost:8082/guess).
     * @return GameReply that includes the game's ID needed for subsequent GET requests.
     */
    @PostMapping
    GameReply startNewGame(@RequestBody GameRange range) {
        return guessingGameApi.startNewGame(range);
    }

    /**
     * Process GET request for a currently running game (e.g.
     * http://localhost:8082/guess/b253c934-50d8-4fc2-bda6-b9dcb70f11c1?guess=50)
     * @param id ID of a currently running game.
     * @param guess User's guess.
     * @return 200: GameReply that includes the result of the guess. 404: If the ID supplied is invalid.
     */
    @GetMapping("/{id}")
    ResponseEntity<GameReply> guessNumber(
            @PathVariable String id,
            @PathParam("guess") int guess
    ) {
        try {
            GameReply gameReply = guessingGameApi.guessNumber(gameRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Game ID is invalid")), guess);
            return new ResponseEntity<>(gameReply, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new GameReply(id, 0, 0, e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * GET database records of all games currently being played.
     * @return Iterable list of Game records.
     */
    @GetMapping("/games")
    Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }
}
