package com.drip.competitionengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "matches",
        indexes = @Index(name = "ix_match_tour_pos",
                columnList = "tournament_id, position"))
@Getter @Setter @NoArgsConstructor
public class Match {

    @Id
    private UUID id;

    /* ---------- служебные поля ---------- */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    @JsonIgnore
    private Tournament tournament;

    @Column(name = "position")
    @JsonIgnore
    private Integer position;

    /* ---------- данные из OpenAPI ---------- */

    private Instant plannedStartTime;
    private Instant plannedEndTime;
    private Instant startedAt;
    private Instant finishedAt;

    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.PREPARED;

    private UUID winner;

    /* parentMatches */
    @ElementCollection
    @CollectionTable(name = "match_parents",
            joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "parent_id")
    private List<UUID> parentMatches = new ArrayList<>();

    /* participants — теперь Embeddable */
    @ElementCollection
    @CollectionTable(name = "match_participants",
            joinColumns = @JoinColumn(name = "match_id"))
    private List<MatchParticipant> participants = new ArrayList<>();

    public void addParticipant(UUID pid) {
        participants.add(new MatchParticipant(pid));
    }

    /** Любое изменение полей → матч считается идущим. */
    public void touchOngoing() {
        if (status == MatchStatus.PREPARED) {
            status = MatchStatus.ONGOING;
            if (startedAt == null) startedAt = Instant.now();
        }
    }

    /** Фиксация победителя + финиша */
    public void finish(UUID winnerId) {
        this.winner     = winnerId;
        this.status     = MatchStatus.FINISHED;
        this.finishedAt = Instant.now();
        if (startedAt == null) startedAt = finishedAt; // страховка
    }
}

