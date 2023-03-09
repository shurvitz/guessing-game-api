package com.guessinggame.component;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.StreamSupport;

/**
 * Scheduled job to purge expired games.
 */
@EnableScheduling
@Component
public class GameRepositoryPurger {
    private final GameRepository gameRepository;
    private final static long INVOKE_PURGE_RATE_MILLIS = 60_000L;
    private final static long KEEP_ALIVE_INTERVAL_SECONDS = 60L;

    GameRepositoryPurger(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Purge games that have not been played for over 60 seconds.
     */
    @Scheduled(fixedRate = INVOKE_PURGE_RATE_MILLIS) // Invoke every 60 seconds
    private void purgeOldGames() {
        StreamSupport.stream(gameRepository.findAll().spliterator(), true)
                .filter(game -> Instant.now().getEpochSecond() -
                        game.getLastModified().getEpochSecond() > KEEP_ALIVE_INTERVAL_SECONDS)
                .forEach(game -> {
                    gameRepository.deleteById(game.getId());
                    System.out.println(game + " (game purged)");
                });
    }
}
