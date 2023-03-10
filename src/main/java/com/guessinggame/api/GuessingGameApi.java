package com.guessinggame.api;

import com.guessinggame.component.Game;
import com.guessinggame.component.GameRange;
import com.guessinggame.component.GameReply;
import com.guessinggame.component.GameRepository;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the APIs business logic.
 */
public class GuessingGameApi {
    private final GameRepository gameRepository;
    public final static int DEFAULT_FROM = 1;
    public final static int DEFAULT_TO = 100;

    /**
     * Constructor
     * @param gameRepository Repository holding all games currently in-progress.
     */
    public GuessingGameApi(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * API for starting a new game.
     * @return GameReply that includes the game's ID needed for the guessNumber API.
     */
    public GameReply startNewGame(GameRange range) {
        int from = range.getFrom();
        int to = range.getTo();
        Game game;

        if (from < to) {
            game = new Game(from, to);
        } else { // Invalid - from must be less than to
            game = new Game(DEFAULT_FROM, DEFAULT_TO);
            System.out.printf(
                    "%s (range specified {from %d to %d} is invalid, using default range {from %d to %d})\n",
                    game, from, to, DEFAULT_FROM, DEFAULT_TO
            );
        }

        gameRepository.save(game);
        System.out.println(game + " (game started)");

        return new GameReply(game.getId(), 0, 0,
                "Guess a number from " + game.getFromRange() + " to " + game.getToRange() + "?");
    }

    /**
     * API for guessing the number in the game currently playing.
     * @param game Current game being played.
     * @param guess User's guess (e.g. 1-100).
     * @return GameReply that includes the result of the guess (e.g. -1 if the number is lower, 1 if it's higher,
     * and 0 if correctly guessed), and the number of guesses already attempted.
     */
    public GameReply guessNumber(@NotNull Game game, int guess) {
        int secretNumber = game.getSecretNumber();

        game.incrementAttempts();
        System.out.println(game + " (guess: " + guess + ")");

        if (guess == secretNumber) {
            gameRepository.deleteById(game.getId());
            System.out.println(game + " (game ended)");
            return new GameReply(game.getId(), game.getGuessingAttempts(), 0,
                    "Congrats!!! You've guessed my number: " + game.getSecretNumber());
        } else if (guess < secretNumber) {
            gameRepository.save(game);
            return new GameReply(game.getId(), game.getGuessingAttempts(), 1, "Higher...");
        } else {
            gameRepository.save(game);
            return new GameReply(game.getId(), game.getGuessingAttempts(), -1, "Lower...");
        }
    }
}
