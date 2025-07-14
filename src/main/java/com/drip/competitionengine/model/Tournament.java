package com.drip.competitionengine.model;

import com.drip.competitionengine.bracket.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "tournaments")
@Getter @Setter @NoArgsConstructor
public class Tournament implements BracketAware {

    /* ---------- PK и базовые поля ---------- */

    @Id
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Sport sport;

    @Enumerated(EnumType.STRING)
    private TournamentType typeTournament;

    @Enumerated(EnumType.STRING)
    private GroupType typeGroup;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "participant_id")
    private List<UUID> participants = new ArrayList<>();

    private String place;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    /* ---------- Связи ---------- */

    // матчи будут храниться отдельно; здесь удобно иметь упорядоченный view
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position")
    @JsonIgnore
    private List<Match> matches = new ArrayList<>();

    /* ---------- Ленивая генерация сетки ---------- */

    @Override
    public Bracket getBracket() {
        // делегируем нужной стратегии
        return BracketStrategies.forType(typeGroup).generate(this);
    }
}
