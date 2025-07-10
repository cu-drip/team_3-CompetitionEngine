package com.drip.competitionengine.controller;

import com.drip.competitionengine.dto.MatchDTO;
import com.drip.competitionengine.dto.ScoreDto;
import com.drip.competitionengine.service.MatchService;
import com.drip.competitionengine.service.TournamentService;
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
    private final TournamentService tourService;
    private final MatchService matchService;

    /* ---------- MATCH MANAGEMENT ---------- */

    @GetMapping("/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getAllMatches() {
        return List.of("aboba", "aboba2");
    }

    @PostMapping("/matches")
    public MatchDTO getMatchById(@RequestBody MatchDTO dto) {
        return matchService.createMatch(dto);
    }


    @GetMapping("/matches/{id}")
    public MatchDTO getTournamentById(@PathVariable UUID id) {
        return matchService.getMatchById(id);
    }

    @PutMapping("/matches/{id}")
    public MatchDTO updateTournament(@PathVariable UUID id, @RequestBody MatchDTO dto) {
        return matchService.updateMatch(id, dto);
    }

    @PatchMapping("/matches/{id}")
    public MatchDTO partialUpdateTournament(@PathVariable UUID id, @RequestBody MatchDTO dto) {
        return matchService.partiallyUpdateMatch(id, dto);
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- TOURNAMENT ---------- */

    @PostMapping("/distribute/{tourId}")
    public ResponseEntity<Void> distribute(@PathVariable UUID tourId) {
        tourService.distribute(tourId);
        return ResponseEntity.noContent().build();           // 204
    }

    /* ---------- MATCH LIFECYCLE ---------- */

    @PostMapping("/start_match/{matchId}")
    public ResponseEntity<String> startMatch(@PathVariable UUID matchId) {
        matchService.startMatch(matchId);
        return ResponseEntity.ok("Started"); // 200
    }

    @GetMapping("/get_match_status/{matchId}")
    public MatchDTO getStatus(@PathVariable UUID matchId) {        // 200
        return new MatchDTO();
    }

    /* ---------- SCORE OPS ---------- */

    @PostMapping("/add_points/{matchId}/{participantId}/{n}")
    public ResponseEntity<ScoreDto> addPts(@PathVariable UUID matchId,
                                           @PathVariable UUID participantId,
                                           @PathVariable int n) {
        int pts = matchService.addPoints(matchId, participantId, n);
        return ResponseEntity.ok(new ScoreDto(matchId, participantId, pts)); // 200
    }

    @PostMapping("/remove_points/{matchId}/{participantId}/{n}")
    public ResponseEntity<ScoreDto> remPts(@PathVariable UUID matchId,
                                           @PathVariable UUID participantId,
                                           @PathVariable int n) {
        int pts = matchService.removePoints(matchId, participantId, n);
        return ResponseEntity.ok(new ScoreDto(matchId, participantId, pts)); // 200
    }

    @PostMapping("/set_winner/{matchId}/{participantId}")
    public ResponseEntity<Void> setWinner(@PathVariable UUID matchId,
                                          @PathVariable UUID participantId) {
        matchService.setWinner(matchId, participantId);
        return ResponseEntity.noContent().build();           // 204
    }
}
