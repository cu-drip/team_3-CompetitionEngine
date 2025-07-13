// src/main/java/com/drip/competitionengine/service/BracketService.java
package com.drip.competitionengine.service;

import com.drip.competitionengine.bracket.Bracket;
import com.drip.competitionengine.bracket.BracketStrategy;
import com.drip.competitionengine.model.GroupType;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.model.Tournament;
import com.drip.competitionengine.repo.MatchRepository;
import com.drip.competitionengine.repo.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Агрегирует все стратегии и отвечает за «жизненный цикл» сетки
 * (чтение, пересоздание, хранение матчей).
 */
@Service
@RequiredArgsConstructor
public class BracketService {
    private final TournamentRepository tourRepo;
    /** Все BracketStrategy, собранные по имени бина. <br>
     *  Важно: имя = enum GroupType.name()  (OLYMPIC, ROUND_ROBIN, SWISS)  */
    private final Map<String, BracketStrategy> strategies;

    private final MatchRepository matchRepo;

    /* -------- публичное API -------- */

    /** Вернуть существующую сетку или сгенерировать, если ещё не строили */
    @Transactional          // без readOnly
    public Bracket getBracket(Tournament tour) {

        // 1️⃣  уже есть в кэше Hibernate?
        if (!tour.getMatches().isEmpty()) {
            return new Bracket(tour.getMatches());
        }

        // 2️⃣  есть в БД?
        List<Match> db = matchRepo.findByTournamentId(
                tour.getId(), Sort.by("position"));

        if (!db.isEmpty()) {
            tour.getMatches().addAll(db);          // подгрузили в сущность
            return new Bracket(db);
        }

        // 3️⃣  первый вызов – генерируем и сохраняем
        return buildAndPersist(tour);
    }


    /** Перегенерировать сетку с нуля (PATCH /bracket/{tourId}) */
    @Transactional
    public Bracket rebuild(Tournament tour) {
        matchRepo.deleteAll(matchRepo.findByTournamentId(tour.getId()));
        return buildAndPersist(tour);
    }

    /* -------- helpers -------- */

    @Transactional
    public Bracket buildAndPersist(Tournament tour) {

        BracketStrategy strat = strategyFor(tour.getTypeGroup());
        Bracket bracket       = strat.generate(tour);   // стратегия добавляет m → tour.matches

        /* каскад ALL сохранит и матчи, и ссылки */
        tourRepo.save(tour);

        return bracket;
    }

    private BracketStrategy strategyFor(GroupType type) {
        BracketStrategy s = strategies.get(type.name());
        if (s == null)
            throw new EntityNotFoundException("No strategy for " + type);
        return s;
    }
}
