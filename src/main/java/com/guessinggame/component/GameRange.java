package com.guessinggame.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameRange {
    @JsonProperty
    private int from;
    @JsonProperty
    private int to;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
