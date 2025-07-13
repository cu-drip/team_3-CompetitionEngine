package com.drip.competitionengine.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class MatchParticipant {

    private UUID id;
    private Integer score = 0;
    private Integer redCards = 0;
    private Integer yellowCards = 0;
    private Integer assists = 0;
    private Integer fouls = 0;
    private Integer sets = 0;
    private Integer knockdowns = 0;

    public MatchParticipant(UUID id) { this.id = id; }
}
