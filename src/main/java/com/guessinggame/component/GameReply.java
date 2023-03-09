package com.guessinggame.component;

public class GameReply {
    private final String gameId;
    private final int guessingAttempts;
    private final int guessStatus;
    private final String replyMessage;

    public GameReply(String gameId, int guessingAttempts, int guessStatus, String replyMessage) {
        this.gameId = gameId;
        this.guessingAttempts = guessingAttempts;
        this.guessStatus = guessStatus;
        this.replyMessage = replyMessage;
    }

    public String getGameId() {
        return gameId;
    }

    public int getGuessingAttempts() {
        return guessingAttempts;
    }

    public int getGuessStatus() {
        return guessStatus;
    }

    public String getReplyMessage() {
        return replyMessage;
    }
}
