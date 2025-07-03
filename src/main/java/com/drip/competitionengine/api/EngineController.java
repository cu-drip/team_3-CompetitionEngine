package com.drip.competitionengine.api;

import com.drip.competitionengine.api.dto.MatchStatusDto;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.service.MatchService;
import com.drip.competitionengine.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class EngineController {
    private final TournamentService tourService;
    private final MatchService      matchService;

    /* ---------- TOURNAMENT ---------- */

    @PostMapping("/distribute/{tourId}")
    public ResponseEntity<Void> distribute(@PathVariable UUID tourId) {
        tourService.distribute(tourId);
        return ResponseEntity.noContent().build();           // 204
    }

    /* ---------- MATCH LIFECYCLE ---------- */

    @PostMapping("/start_match/{matchId}")
    public ResponseEntity<MatchStatusDto> startMatch(@PathVariable UUID matchId) {
        matchService.startMatch(matchId);
        return ResponseEntity.ok(toDto(matchService.getStatus(matchId))); // 200 + статус
    }

    @GetMapping("/get_match_status/{matchId}")
    public MatchStatusDto getStatus(@PathVariable UUID matchId) {        // 200
        return toDto(matchService.getStatus(matchId));
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

    /* ---------- helper ---------- */
    private static MatchStatusDto toDto(Match m){
        return new MatchStatusDto(
                m.getStartedAt(),
                m.getPartition1Id(), m.getPartition2Id(),
                m.getPartition1Points(), m.getPartition2Points(),
                m.getWinnerId());
    }
}
