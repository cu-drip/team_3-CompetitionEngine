package com.drip.competitionengine.service;

import com.drip.competitionengine.dto.MatchDTO;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.repo.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepo;

    public List<MatchDTO> getAllMatches() {
        return matchRepo.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MatchDTO createMatch(MatchDTO dto) {
        Match match = toModel(dto);
        return toDTO(matchRepo.save(match));
    }

    public MatchDTO getMatchById(UUID id) {
        Match match = matchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found: " + id));
        return toDTO(match);
    }

    public MatchDTO updateMatch(UUID id, MatchDTO dto) {
        Match match = matchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found: " + id));

        match.setTourId(dto.getMatchId());
        match.setStartedAt(dto.getStartedAt());
        match.setPosition(dto.getPosition());
        match.setPartition1Id(dto.getParticipant1());
        match.setPartition2Id(dto.getParticipant2());
        match.setPartition1Points(dto.getP1Points());
        match.setPartition2Points(dto.getP2Points());
        match.setWinnerId(dto.getWinner());

        return toDTO(matchRepo.save(match));
    }

    public MatchDTO partiallyUpdateMatch(UUID id, MatchDTO dto) {
        Match match = matchRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found: " + id));

        if (dto.getTournamentId() != null) {
            match.setTourId(dto.getMatchId());
        }
        if (dto.getStartedAt() != null) {
            match.setStartedAt(dto.getStartedAt());
        }
        if (dto.getPosition() != null) {
            match.setPosition(dto.getPosition());
        }
        if (dto.getParticipant1() != null) {
            match.setPartition1Id(dto.getParticipant1());
        }
        if (dto.getParticipant2() != null) {
            match.setPartition2Id(dto.getParticipant2());
        }
        if (dto.getP1Points() != null) {
            match.setPartition1Points(dto.getP1Points());
        }
        if (dto.getP2Points() != null) {
            match.setPartition2Points(dto.getP2Points());
        }
        if (dto.getWinner() != null) {
            match.setWinnerId(dto.getWinner());
        }

        return toDTO(matchRepo.save(match));
    }

    public void deleteMatch(UUID id) {
        matchRepo.deleteById(id);
    }


    @Transactional
    public void startMatch(UUID matchId) {
        Match m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        if (m.getStartedAt() != null) throw new IllegalStateException("Already started");
        m.setStartedAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Match getStatus(UUID matchId) {
        return matchRepo.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
    }

    @Transactional
    public int addPoints(UUID matchId, UUID participantId, int n) {
        Match m = getActive(matchId);
        m.setPartitionPointsById(participantId, m.getPartitionPointsById(participantId) + n);
        return m.getPartitionPointsById(participantId);
    }

    @Transactional
    public int removePoints(UUID matchId, UUID participantId, int n) {
        Match m = getActive(matchId);
        m.setPartitionPointsById(participantId, m.getPartitionPointsById(participantId) - n);
        return m.getPartitionPointsById(participantId);
    }

    @Transactional
    public void setWinner(UUID matchId, UUID winner) {
        Match m = getActive(matchId);
        m.setWinnerId(winner);
    }

    private Match getActive(UUID id) {
        Match m = matchRepo.findById(id).orElseThrow();
        if (!m.isActive()) throw new IllegalStateException("Match not active");
        return m;
    }

    private MatchDTO toDTO(Match model) {
        MatchDTO dto = new MatchDTO();

        dto.setMatchId(model.getId());
        dto.setTournamentId(model.getTourId());
        dto.setStartedAt(model.getStartedAt());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setPosition(model.getPosition());
        dto.setParticipant1(model.getPartition1Id());
        dto.setParticipant2(model.getPartition2Id());
        dto.setP1Points(model.getPartition1Points());
        dto.setP2Points(model.getPartition2Points());
        dto.setWinner(model.getWinnerId());

        return dto;
    }

    private Match toModel(MatchDTO dto) {
        Match model = new Match();

        model.setId(dto.getMatchId() != null ? dto.getMatchId() : UUID.randomUUID());
        model.setTourId(dto.getTournamentId());
        model.setStartedAt(dto.getStartedAt());
        model.setPosition(dto.getPosition());
        model.setPartition1Id(dto.getParticipant1());
        model.setPartition2Id(dto.getParticipant2());
        model.setPartition1Points(dto.getP1Points());
        model.setPartition2Points(dto.getP2Points());
        model.setWinnerId(dto.getWinner());

        return model;
    }
}
