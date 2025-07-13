// src/main/java/com/drip/competitionengine/dto/StatsParticipant.java
package com.drip.competitionengine.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StatsParticipant {

    private UUID    participantId;
    private String  participantType;   // "player" | "team"

    private Integer points;
    private Boolean isWinner;          // <-- REQUIRED by stats-service

    // дополнительные поля — nullable
    private Integer goals;
    private Integer assists;
    private Integer fouls;
    private Integer yellowCards;
    private Integer redCards;
    private Integer knockdowns;
    private Integer submissions;
    private Integer setsWon;
    private Float   timePlayed;        // minutes
}
