package com.drip.competitionengine.bracket;

import com.drip.competitionengine.exception.NotEnoughParticipantsException;
import com.drip.competitionengine.model.*;
import com.drip.competitionengine.repo.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Component("OLYMPIC")
@RequiredArgsConstructor
public class OlympicStrategy implements BracketStrategy {

    private final MatchRepository matchRepo;

    @Override
    @Transactional          // нужны сохранения внутри одного юнита работы
    public Bracket generate(Tournament tour) {
        /* 1. Список участников берём прямо из сущности */
        List<UUID> parts = new ArrayList<>(tour.getParticipants());
        if (parts.size() < 2)
            throw new NotEnoughParticipantsException();

        /* 2. Дополняем до степени двойки BYE-слотами (null) */
        int size = nextPow2(parts.size());
        while (parts.size() < size) parts.add(null);

        /* 3. Строим сетку снизу вверх */
        List<Match> round = leafRound(parts, tour);
        List<Match> all   = new ArrayList<>(round);

        while (round.size() > 1) {
            round = parentRound(round, tour);
            all.addAll(round);
        }

        matchRepo.saveAll(all);      // фиксируем в БД
        return new Bracket(all);     // отдаём клиенту
    }

    /* ---------- helpers ---------- */

    private int nextPow2(int n) { return Integer.highestOneBit(n - 1) << 1; }

    private List<Match> leafRound(List<UUID> parts, Tournament t) {
        List<Match> res = new ArrayList<>();
        for (int i = 0; i < parts.size(); i += 2) {
            res.add(makeMatch(t, i / 2, parts.get(i), parts.get(i + 1), List.of()));
        }
        return res;
    }

    private List<Match> parentRound(List<Match> children, Tournament t) {
        List<Match> res = new ArrayList<>();
        for (int i = 0; i < children.size(); i += 2) {
            List<UUID> parents = List.of(children.get(i).getId(),
                    children.get(i + 1).getId());
            res.add(makeMatch(t, i / 2, null, null, parents));
        }
        return res;
    }

    private Match makeMatch(Tournament t,
                            int pos,
                            UUID p1,
                            UUID p2,
                            List<UUID> parents) {

        Match m = new Match();
        m.setId(UUID.randomUUID());
        m.setTournament(t);
        m.setPosition(pos);
        m.setPlannedStartTime(t.getStartTime());
        m.setPlannedEndTime(t.getStartTime().plusSeconds(3600));
        m.setStatus(MatchStatus.PREPARED);
        m.setParentMatches(parents);

        if (p1 != null) m.addParticipant(p1);
        if (p2 != null) m.addParticipant(p2);
        t.getMatches().add(m);
        return m;
    }
}
