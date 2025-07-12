// src/main/java/com/drip/competitionengine/dto/MatchParticipantDto.java
package com.drip.competitionengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Плоская копия MatchParticipant для ответа API.
 * Поля 1-в-1 совпадают с OpenAPI-контрактом.
 */
@Getter @Setter
public class MatchParticipantDto {

    @JsonIgnore                 // не сериализовать в JSON
    public boolean hasStats() {
        return score       != null ||
                redCards    != null ||
                yellowCards != null ||
                assists     != null ||
                fouls       != null ||
                sets        != null ||
                knockdowns  != null;
    }

    private UUID id;
    private Integer score;
    private Integer redCards;
    private Integer yellowCards;
    private Integer assists;
    private Integer fouls;
    private Integer sets;
    private Integer knockdowns;
}
