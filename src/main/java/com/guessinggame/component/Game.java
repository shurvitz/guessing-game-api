package com.guessinggame.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Entity
public class Game {
    /**
     * The default start range for the game if not specified in the constructor.
     */
    public final static int DEFAULT_FROM = 1;

    /**
     * The default end range for the game if not specified in the constructor.
     */
    public final static int DEFAULT_TO = 100;

    @Id
    private String id;
    private int guessingAttempts;
    @JsonIgnore // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) can also be used to block serialization only.
    private int secretNumber;
    private int fromRange;
    private int toRange;
    private Instant lastModified;

    public Game() {
        this(DEFAULT_FROM, DEFAULT_TO);
    }

    public Game(int fromRange, int toRange) {
        id = UUID.randomUUID().toString();
        guessingAttempts = 0;
        secretNumber = new Random().nextInt(fromRange, toRange + 1);
        this.fromRange = fromRange;
        this.toRange = toRange;
        lastModified = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getGuessingAttempts() {
        return guessingAttempts;
    }

    public void setGuessingAttempts(int guessingAttempts) {
        this.guessingAttempts = guessingAttempts;
    }

    public int getSecretNumber() {
        return secretNumber;
    }

    public void setSecretNumber(int secretNumber) {
        this.secretNumber = secretNumber;
    }

    public int getFromRange() {
        return fromRange;
    }

    public void setFromRange(int fromRange) {
        this.fromRange = fromRange;
    }

    public int getToRange() {
        return toRange;
    }

    public void setToRange(int toRange) {
        this.toRange = toRange;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public void incrementAttempts() {
        ++guessingAttempts;
        lastModified = Instant.now();
    }

    @Override
    public String toString() {
        return lastModified.toString() + "  GAME --- ID: " + id + ", Attempt: " + guessingAttempts;
    }
}
