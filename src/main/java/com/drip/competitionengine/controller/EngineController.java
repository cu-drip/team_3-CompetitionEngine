package com.drip.competitionengine.controller;

import com.drip.competitionengine.bracket.Bracket;
import com.drip.competitionengine.dto.BracketUploadRequest;
import com.drip.competitionengine.dto.MatchDtoOut;
import com.drip.competitionengine.dto.TourCreateRequest;
import com.drip.competitionengine.model.Tournament;
import com.drip.competitionengine.service.MatchService;
import com.drip.competitionengine.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EngineController {

    private final TournamentService tournamentService;
    private final MatchService matchService;

    /* -------------------------------------------------
     * 1.  TOURNAMENTS
     * ------------------------------------------------- */

    /** POST /tour – создание турнира */
    @PostMapping("/tour")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tournament> createTour(
            @Valid @RequestBody TourCreateRequest body) {

        Tournament saved = tournamentService.create(body);
        return ResponseEntity.status(201).body(saved);   // 201 Created
    }

    /** GET /tour/{id} */
    @GetMapping("/tour/{tourId}")
    public Tournament getTour(@PathVariable UUID tourId) {
        return tournamentService.getById(tourId);
    }

    /** PUT /tour/{id} – полная замена */
    @PutMapping("/tour/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Tournament replaceTour(@PathVariable UUID tourId,
                                  @Valid @RequestBody TourCreateRequest body) {
        return tournamentService.replace(tourId, body);
    }

    /** DELETE /tour/{id} */
    @DeleteMapping("/tour/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTour(@PathVariable UUID tourId) {
        tournamentService.delete(tourId);
        return ResponseEntity.noContent().build();       // 204
    }

    /* -------------------------------------------------
     * 2.  BRACKETS  (one per tournament)
     * ------------------------------------------------- */

    /** GET /bracket/{tourId} */
    @GetMapping("/bracket/{tourId}")
    public Bracket getBracket(@PathVariable UUID tourId) {
        return tournamentService.getBracket(tourId);     // генерирует или читает из БД
    }

    /** PATCH /bracket/{tourId} – клиент полностью задаёт новую сетку */
    @PatchMapping("/bracket/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Bracket replaceBracket(@PathVariable UUID tourId,
                                  @RequestBody BracketUploadRequest body) {

        return tournamentService.replaceBracket(tourId, body.getMatches());
    }



    /* -------------------------------------------------
     * 3.  MATCHES   – nested under tournament
     * ------------------------------------------------- */

    /** GET /tour/{tourId}/matches – список матчей турнира */
    @GetMapping("/tour/{tourId}/matches")
    public List<MatchDtoOut> listMatches(@PathVariable UUID tourId) {
        return matchService.listByTournament(tourId);
    }

    /** POST /tour/{tourId}/matches – ручное добавление матча */
    @PostMapping("/tour/{tourId}/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchDtoOut> createMatch(@PathVariable UUID tourId,
                                                   @RequestBody MatchDtoOut body) {
        MatchDtoOut saved = matchService.createInTournament(tourId, body);
        return ResponseEntity.status(201).body(saved);
    }

    /* ---- одиночный матч внутри турнира ---- */

    /** GET /tour/{tourId}/matches/{matchId} */
    @GetMapping("/tour/{tourId}/matches/{matchId}")
    public MatchDtoOut getMatchInTour(@PathVariable UUID tourId,
                                      @PathVariable UUID matchId) {
        return matchService.getInTournament(tourId, matchId);
    }

    /** PUT /tour/{tourId}/matches/{matchId} – полная замена */
    @PutMapping("/tour/{tourId}/matches/{matchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MatchDtoOut replaceMatch(@PathVariable UUID tourId,
                                    @PathVariable UUID matchId,
                                    @RequestBody MatchDtoOut body) {
        return matchService.replace(tourId, matchId, body);
    }

    /** PATCH /tour/{tourId}/matches/{matchId} – частичное обновление */
    @PatchMapping("/tour/{tourId}/matches/{matchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public MatchDtoOut patchMatch(@PathVariable UUID tourId,
                                  @PathVariable UUID matchId,
                                  @RequestBody MatchDtoOut body) {
        return matchService.patch(tourId, matchId, body);
    }

    /** DELETE /tour/{tourId}/matches/{matchId} */
    @DeleteMapping("/tour/{tourId}/matches/{matchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID tourId,
                                            @PathVariable UUID matchId) {
        matchService.delete(tourId, matchId);
        return ResponseEntity.noContent().build();
    }

    /* -------------------------------------------------
     * 4.  GLOBAL read-only access to a match
     * ------------------------------------------------- */

    /** GET /matches/{matchId} – каноническая ссылка */
    @GetMapping("/matches/{matchId}")
    public MatchDtoOut getMatchGlobal(@PathVariable UUID matchId) {
        MatchDtoOut dto = matchService.getGlobal(matchId);
        // добавляем canonical URL, как требует контракт
        dto.setCanonical("/api/v1/tour/" + dto.getTournamentId()
                + "/matches/" + dto.getMatchId());
        return dto;
    }
}
