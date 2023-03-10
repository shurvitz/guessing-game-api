package com.guessinggame.controller;

import com.guessinggame.component.Game;
import com.guessinggame.component.GameRange;
import com.guessinggame.component.GameReply;
import com.guessinggame.component.GameRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GuessingGameControllerTest {
    @Autowired GameRepository gameRepository;
    @Autowired WebTestClient client;

    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
    }

    @AfterEach
    public void teardown() {
        gameRepository.deleteAll();
    }

    @Order(1)
    @Test
    void startNewGameTest() {
        System.out.println("Test --- GuessingGameControllerTest:startNewGameTest");

        GameReply gameReply = startNewGame();

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        String gameId = gameReply.getGameId();
        assertNotNull(gameId, "Expected game ID to be non-null");
        Optional<Game> gameRecord = gameRepository.findById(gameId);
        assertTrue(gameRecord.isPresent(), "Expected to find game in repo by ID");
        Game game = gameRecord.get();
        assertEquals(Game.DEFAULT_FROM, game.getFromRange(), "Expected from ranges to equal");
        assertEquals(Game.DEFAULT_TO, game.getToRange(), "Expected to ranges to equal");
        int secretNumber = game.getSecretNumber();
        assertTrue(secretNumber >= Game.DEFAULT_FROM && secretNumber <= Game.DEFAULT_TO,
                "Expected secret number to be in range of default start and end");
        assertEquals(0, game.getGuessingAttempts(), "Expected 0 guessing attempts");
        assertEquals(game.getGuessingAttempts(), gameReply.getGuessingAttempts(),
                "Expected guessing attempts to equal");
    }

    @Order(2)
    @Test
    void guessNumberHigherTest() {
        System.out.println("Test --- GuessingGameControllerTest:guessNumberHigherTest");

        GameReply gameReply = startNewGame();

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        String gameId = gameReply.getGameId();
        assertNotNull(gameId, "Expected game ID to be non-null");

        gameReply = guessNumber(gameId, Game.DEFAULT_FROM - 1);

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        assertEquals(gameId, gameReply.getGameId(), "Expected game IDs to equal");
        assertEquals(1, gameReply.getGuessingAttempts(), "Expected guessing attempts to be 1");
        assertEquals(1, gameReply.getGuessStatus(), "Expected guess status of 1 (higher)");
    }

    @Order(3)
    @Test
    void guessNumberLowerTest() {
        System.out.println("Test --- GuessingGameControllerTest:guessNumberLowerTest");

        GameReply gameReply = startNewGame();

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        String gameId = gameReply.getGameId();
        assertNotNull(gameId, "Expected game ID to be non-null");

        gameReply = guessNumber(gameId, Game.DEFAULT_TO + 1);

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        assertEquals(gameId, gameReply.getGameId(), "Expected game IDs to equal");
        assertEquals(1, gameReply.getGuessingAttempts(), "Expected guessing attempts to be 1");
        assertEquals(-1, gameReply.getGuessStatus(), "Expected guess status of -1 (lower)");
    }

    @Order(4)
    @Test
    void guessNumberTest() {
        System.out.println("Test --- GuessingGameControllerTest:guessNumberTest");

        GameReply gameReply = startNewGame();

        assertNotNull(gameReply, "Expected GameReply object to be non-null");
        String gameId = gameReply.getGameId();
        assertNotNull(gameId, "Expected game ID to be non-null");

        int attempts = 0;
        int low = Game.DEFAULT_FROM;
        int high = Game.DEFAULT_TO;

        do {
            int guess = (low + high) / 2;
            gameReply = guessNumber(gameId, guess);
            ++attempts;

            assertNotNull(gameReply, "Expected GameReply object to be non-null");
            assertEquals(gameId, gameReply.getGameId(), "Expected game IDs to equal");
            assertEquals(attempts, gameReply.getGuessingAttempts(), "Expected guessing attempts to be 1");

            if (gameReply.getGuessStatus() > 0)
                low = guess + 1;
            else if (gameReply.getGuessStatus() < 0)
                high = guess - 1;
        } while (low <= high && gameReply.getGuessStatus() != 0);

        assertEquals(0, gameReply.getGuessStatus(), "Expected guess status of 0 (found number)");
    }

    @Order(5)
    @Test
    void getListOfGamesTest() {
        System.out.println("Test --- GuessingGameControllerTest:getListOfGamesTest");

        GameReply game1Reply = startNewGame();
        GameReply game2Reply = startNewGame();
        Set<String> games = new HashSet<>(Set.of(game1Reply.getGameId(), game2Reply.getGameId()));
        Iterable<Game> iterable = client.get()
                .uri("/guess/games")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Game.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(iterable, "Expected Iterable object to be non-null");

        Iterator<Game> iterator = iterable.iterator();

        // Game 1
        assertTrue(iterator.hasNext(), "Expected iterable to contain 1st Game object");
        assertTrue(games.contains(iterator.next().getId()), "Expected 1st game to exist in iterable");

        // Game 2
        assertTrue(iterator.hasNext(), "Expected iterable to contain 2nd Game object");
        assertTrue(games.contains(iterator.next().getId()), "Expected 2nd game to exist in iterable");

        // Game 3
        assertFalse(iterator.hasNext(), "Expected no more Game objects in iterable");
    }

    private GameReply startNewGame() {
        return client.post()
                .uri("/guess")
                .body(Mono.just(new GameRange()), GameRange.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameReply.class)
                .returnResult()
                .getResponseBody();
    }

    private GameReply guessNumber(String id, int number) {
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/guess/{id}")
                        .queryParam("guess", number)
                        .build(id))
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameReply.class)
                .returnResult()
                .getResponseBody();
    }
}