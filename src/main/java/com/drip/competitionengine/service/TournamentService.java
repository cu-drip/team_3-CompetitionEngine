package com.drip.competitionengine.service;

import com.drip.competitionengine.bracket.Bracket;
import com.drip.competitionengine.dto.MatchDtoOut;
import com.drip.competitionengine.dto.TourCreateRequest;
import com.drip.competitionengine.mapper.MatchMapper;
import com.drip.competitionengine.model.Match;
import com.drip.competitionengine.model.Tournament;
import com.drip.competitionengine.repo.MatchRepository;
import com.drip.competitionengine.repo.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository repo;
    private final BracketService bracketService;
    private final MatchRepository matchRepo;
    private final MatchMapper mapper;
    @Transactional
    public Tournament create(TourCreateRequest req) {
        Tournament t = new Tournament();
        t.setId( req.getId() != null ? req.getId() : UUID.randomUUID() );
        apply(req, t);
        t.setMatches(List.of());
        return repo.save(t);  // возвращаем управляемый объект с ID
    }


    public Tournament getById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tournament not found"));
    }

    @Transactional
    public Tournament replace(UUID id, TourCreateRequest body) {
        Tournament t = getById(id);

        apply(body, t);

        return repo.save(t);
    }

    private void apply(TourCreateRequest body, Tournament t) {
        t.setTitle(body.getTitle());
        t.setDescription(body.getDescription());
        t.setSport(body.getSport());
        t.setTypeTournament(body.getTypeTournament());
        t.setTypeGroup(body.getTypeGroup());
        t.setStartTime(body.getStartTime());
        t.setMaxParticipants(body.getMaxParticipants());
        t.setPlace(body.getPlace());
        t.setOrganizerId(body.getOrganizerId());
        t.setParticipants(body.getParticipants());
    }

    @Transactional
    public void delete(UUID id) { repo.deleteById(id); }

    public Bracket getBracket(UUID tourId) {
        Tournament t = getById(tourId);
        return bracketService.getBracket(t);
    }

    @Transactional
    public Bracket replaceBracket(UUID tourId, List<MatchDtoOut> body) {

        Tournament tour = getById(tourId);

        /* 1. чистим старые матчи */
        matchRepo.deleteAll(matchRepo.findByTournamentId(tourId));

        /* 2. маппинг body → entity */
        List<Match> toSave = body.stream()
                .map(dto -> {
                    Match m = mapper.toEntity(dto);
                    if (m.getId() == null) {
                        m.setId(UUID.randomUUID());
                    }
                    m.setTournament(tour);
                    return m;
                })
                .toList();

        List<Match> saved = matchRepo.saveAll(toSave);

        /* 3. возвращаем свежую сетку */
        return new Bracket(saved);
    }


}
