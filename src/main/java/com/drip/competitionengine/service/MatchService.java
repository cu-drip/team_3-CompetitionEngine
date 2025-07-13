package com.drip.competitionengine.service;

import com.drip.competitionengine.dto.MatchDtoOut;
import com.drip.competitionengine.dto.MatchParticipantDto;
import com.drip.competitionengine.dto.StatsPayload;
import com.drip.competitionengine.mapper.MatchMapper;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.model.MatchParticipant;
import com.drip.competitionengine.model.MatchStatus;
import com.drip.competitionengine.model.Tournament;
import com.drip.competitionengine.repo.MatchRepository;
import com.drip.competitionengine.repo.TournamentRepository;
import com.drip.competitionengine.stats.StatsClient;
import com.drip.competitionengine.stats.StatsMapper;
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
    private final StatsMapper  statsMapper;            // MapStruct → JSON
    private final StatsClient statsClient;            // WebClient wrapper
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
    public MatchDtoOut patch(UUID tourId, UUID matchId, MatchDtoOut dto) {

        Match entity = getInTournamentEntity(tourId, matchId);

        /* ------------ 0. фиксируем старое состояние ------------ */
        MatchStatus oldStatus = entity.getStatus();
        UUID        oldWinner = entity.getWinner();

        /* ─── 1. обычные поля ─── */
        mapper.patch(entity, dto);                    // null-ы игнорируются

        /* ─── 2. участники: point-merge по ID ─── */
        if (dto.getParticipants() != null) {
            for (MatchParticipantDto pDto : dto.getParticipants()) {

                MatchParticipant pEnt = entity.getParticipants().stream()
                        .filter(p -> p.getId().equals(pDto.getId()))
                        .findFirst()
                        .orElseGet(() -> {              // если участника ещё нет
                            MatchParticipant np = new MatchParticipant(pDto.getId());
                            entity.getParticipants().add(np);
                            return np;
                        });

                if (pDto.getScore()       != null) pEnt.setScore(pDto.getScore());
                if (pDto.getRedCards()    != null) pEnt.setRedCards(pDto.getRedCards());
                if (pDto.getYellowCards() != null) pEnt.setYellowCards(pDto.getYellowCards());
                if (pDto.getAssists()     != null) pEnt.setAssists(pDto.getAssists());
                if (pDto.getFouls()       != null) pEnt.setFouls(pDto.getFouls());
                if (pDto.getSets()        != null) pEnt.setSets(pDto.getSets());
                if (pDto.getKnockdowns()  != null) pEnt.setKnockdowns(pDto.getKnockdowns());
            }
        }

        /* ------------ 3. логика статуса / winner --------------- */
        MatchStatus newStatus = entity.getStatus();
        UUID        newWinner = entity.getWinner();

        // 3-а. статус явно прислали
        if (dto.getStatus() != null) {
            switch (newStatus) {
                case PREPARED -> {
                    entity.setStartedAt(null);
                    entity.setFinishedAt(null);
                    entity.setWinner(null);
                }
                case ONGOING -> {
                    entity.setWinner(null);
                    entity.touchOngoing();
                    entity.setFinishedAt(null);
                }
                case FINISHED -> { /* winner должен быть - проверка ниже */ }
            }
        }

        // 3-б. winner обнулили
        if (oldWinner != null && newWinner == null) {
            entity.setFinishedAt(null);
            entity.setStatus(MatchStatus.ONGOING);
            newStatus = MatchStatus.ONGOING;
        }

        // 3-в. winner Поставили / изменили  ➜  ШЛЁМ СТАТИСТИКУ
        if (newWinner != null && !newWinner.equals(oldWinner)) {
            entity.finish(newWinner);               // FINISHED + finishedAt
            propagateWinner(entity);

            /* ---- PUSH to statistic-service -------------------- */
            StatsPayload payload = statsMapper.toPayload(
                    entity.getTournament(), List.of(entity));
            statsClient.send(payload);

            newStatus = MatchStatus.FINISHED;
        }

        /* ------------ 4. авто-ONGOING ------------------------- */
        boolean statsChanged  = dto.getParticipants() != null;
        boolean statusForced  = dto.getStatus() != null;

        if (!statusForced && statsChanged && newStatus == MatchStatus.PREPARED) {
            entity.touchOngoing();                  // ONGOING + startedAt
        }

        return mapper.toDto(entity);                // flush по @Transactional
    }



    private void propagateWinner(Match finished) {

        List<Match> children = matchRepo.findChildren(
                finished.getTournament().getId(), finished.getId());

        for (Match ch : children) {
            /* не дублируем, если уже добавлен */
            boolean already =
                    ch.getParticipants().stream()
                            .anyMatch(p -> p.getId().equals(finished.getWinner()));

            if (!already) ch.addParticipant(finished.getWinner());
        }
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
