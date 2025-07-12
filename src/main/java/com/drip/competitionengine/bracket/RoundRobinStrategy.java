// src/main/java/com/drip/competitionengine/bracket/RoundRobinStrategy.java
package com.drip.competitionengine.bracket;

import com.drip.competitionengine.exception.NotEnoughParticipantsException;
import com.drip.competitionengine.model.*;
import com.drip.competitionengine.repo.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * ROUND_ROBIN – каждый играет с каждым один раз.
 * <pre>
 * N участников →  N·(N-1)/2  матчей
 * Если N нечётное — добавляем BYE-слот (никто не играет в этот тур).
 * Расстановка пар по «круговому алгоритму» («circle method»).
 * </pre>
 */
@Component("ROUND_ROBIN")
@RequiredArgsConstructor
public class RoundRobinStrategy implements BracketStrategy {

    private final MatchRepository matchRepo;

    @Override
    @Transactional
    public Bracket generate(Tournament tour) {

        /* 1. список участников */
        List<UUID> parts = new ArrayList<>(tour.getParticipants());
        if (parts.size() < 2) throw new NotEnoughParticipantsException();

        /* 2. нечётное – добавляем BYE (null) */
        boolean hasBye = parts.size() % 2 != 0;
        if (hasBye) parts.add(null);                // null — свободный тур

        int n = parts.size();
        int rounds = n - 1;
        List<Match> all = new ArrayList<>();
        Instant base = tour.getStartTime();         // старт первой пары

        /* 3. круговой алгоритм */
        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < n / 2; i++) {

                UUID home = parts.get(i);
                UUID away = parts.get(n - 1 - i);
                if (home == null || away == null) continue; // BYE

                int pos = r * (n / 2) + i;          // уникальная позиция
                Instant start = base.plusSeconds(r * 3600); // 1 час шаг

                all.add(makeMatch(tour, pos, home, away, start));
            }
            /* «вращаем» список по часовой стрелке, кроме первого элемента */
            Collections.rotate(parts.subList(1, n), 1);
        }

        matchRepo.saveAll(all);
        return new Bracket(all);
    }

    /* ---------- helpers ---------- */

    private Match makeMatch(Tournament t,
                            int position,
                            UUID p1,
                            UUID p2,
                            Instant start) {

        Match m = new Match();
        m.setId(UUID.randomUUID());
        m.setTournament(t);
        m.setPosition(position);
        m.setPlannedStartTime(start);
        m.setPlannedEndTime(start.plusSeconds(3600));
        m.setStatus(MatchStatus.PREPARED);
        m.setParentMatches(List.of());      // RR – нет зависимостей

        m.addParticipant(p1);
        m.addParticipant(p2);

        t.getMatches().add(m);
        return m;
    }
}
