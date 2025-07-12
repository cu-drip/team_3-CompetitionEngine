package com.drip.competitionengine.service;

import com.drip.competitionengine.dto.MatchDtoOut;
import com.drip.competitionengine.mapper.MatchMapper;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.model.Tournament;
import com.drip.competitionengine.repo.MatchRepository;
import com.drip.competitionengine.repo.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Полный набор операций над матчами, отражающий OpenAPI-контракт и новый EngineController.
 */
@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository      matchRepo;
    private final TournamentRepository tourRepo;
    private final MatchMapper          mapper;          // MapStruct

    /* ------------------------------------------------------------------
     * 1.  Список всех матчей конкретного турнира
     * ------------------------------------------------------------------ */
    @Transactional(readOnly = true)
    public List<MatchDtoOut> listByTournament(UUID tourId) {
        return mapper.toDto(
                matchRepo.findByTournamentId(
                        tourId, Sort.by("position").ascending()));
    }

    /* ------------------------------------------------------------------
     * 2.  Создать матч «внутри» турнира
     * ------------------------------------------------------------------ */
    @Transactional
    public MatchDtoOut createInTournament(UUID tourId, MatchDtoOut body) {
        Tournament tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        Match entity = mapper.toEntity(body);
        entity.setId(UUID.randomUUID());
        entity.setTournament(tour);

        return mapper.toDto(matchRepo.save(entity));
    }

    /* ------------------------------------------------------------------
     * 3.  Получить один матч по ID в пределах турнира
     * ------------------------------------------------------------------ */
    @Transactional(readOnly = true)
    public MatchDtoOut getInTournament(UUID tourId, UUID matchId) {
        return mapper.toDto(getInTournamentEntity(tourId, matchId));
    }

    /* ------------------------------------------------------------------
     * 4.  Полная замена (PUT) — все поля из DTO перезаписываются
     * ------------------------------------------------------------------ */
    @Transactional
    public MatchDtoOut replace(UUID tourId, UUID matchId, MatchDtoOut body) {
        Tournament tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));

        Match entity = mapper.toEntity(body);
        entity.setId(matchId);
        entity.setTournament(tour);

        return mapper.toDto(matchRepo.save(entity));
    }

    /* ------------------------------------------------------------------
     * 5.  Частичное обновление (PATCH)
     * ------------------------------------------------------------------ */
    @Transactional
    public MatchDtoOut patch(UUID tourId, UUID matchId, MatchDtoOut body) {
        Match entity = getInTournamentEntity(tourId, matchId);
        mapper.patch(entity, body);                      // @MappingTarget patch-метод
        return mapper.toDto(matchRepo.save(entity));
    }

    /* ------------------------------------------------------------------
     * 6.  Удалить матч
     * ------------------------------------------------------------------ */
    @Transactional
    public void delete(UUID tourId, UUID matchId) {
        Match entity = getInTournamentEntity(tourId, matchId);
        matchRepo.delete(entity);
    }

    /* ------------------------------------------------------------------
     * 7.  Глобальный read-only доступ к матчу (без контекста турнира)
     * ------------------------------------------------------------------ */
    @Transactional(readOnly = true)
    public MatchDtoOut getGlobal(UUID matchId) {
        return mapper.toDto(
                matchRepo.findById(matchId)
                         .orElseThrow(() -> new EntityNotFoundException("Match not found")));
    }

    /* ========= PRIVATE HELPERS ========= */

    private Match getInTournamentEntity(UUID tourId, UUID matchId) {
        return matchRepo.findById(matchId)
                .filter(m -> m.getTournament().getId().equals(tourId))
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
    }
}
