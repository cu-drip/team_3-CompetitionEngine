// src/main/java/com/drip/competitionengine/stats/StatsMapper.java
package com.drip.competitionengine.stats;

import com.drip.competitionengine.dto.*;
import com.drip.competitionengine.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatsMapper {

    /* ------------------------------------------------------------------
     *  ===  ЕДИНСТВЕННЫЙ публичный метод, который зовёт сервис  ===
     * ------------------------------------------------------------------ */
    default StatsPayload toPayload(Tournament t, List<Match> matches) {

        StatsPayload pl = new StatsPayload();
        pl.setTournamentId(t.getId());
        pl.setSport(t.getSport().name().toLowerCase());
        pl.setTournamentType(t.getTypeGroup().name().toLowerCase());

        List<StatsMatch> smList = new ArrayList<>();
        for (Match m : matches) {
            smList.add(toStatsMatch(t, m));
        }
        pl.setMatches(smList);

        return pl;
    }

    /* ------------------------------------------------------------------ */
    /*  --------  private helpers, MapStruct генерировать не надо -------- */
    /* ------------------------------------------------------------------ */

    private StatsMatch toStatsMatch(Tournament t, Match m) {

        StatsMatch sm = new StatsMatch();
        sm.setMatchId(m.getId());

        List<StatsParticipant> spList = new ArrayList<>();
        for (MatchParticipant p : m.getParticipants()) {
            spList.add(toStatsParticipant(t, m, p));
        }
        sm.setParticipants(spList);

        return sm;
    }

    private StatsParticipant toStatsParticipant(Tournament t, Match m, MatchParticipant p) {

        StatsParticipant sp = new StatsParticipant();

        /* --- базовые поля --- */
        sp.setParticipantId(p.getId());
        sp.setParticipantType(
                t.getTypeTournament() == TournamentType.TEAM ? "team" : "player");
        sp.setPoints(p.getScore() == null ? 0 : p.getScore());
        sp.setIsWinner(m.getWinner() != null && m.getWinner().equals(p.getId()));

        /* --- спортивная статистика --- */
        sp.setGoals      (p.getScore()       == null ? 0 : p.getScore());     // футбольные «голы»
        sp.setAssists    (p.getAssists()     == null ? 0 : p.getAssists());
        sp.setFouls      (p.getFouls()       == null ? 0 : p.getFouls());
        sp.setYellowCards(p.getYellowCards() == null ? 0 : p.getYellowCards());
        sp.setRedCards   (p.getRedCards()    == null ? 0 : p.getRedCards());
        sp.setKnockdowns (p.getKnockdowns()  == null ? 0 : p.getKnockdowns());
        sp.setSubmissions(0);                      // пока нечего заполнять
        sp.setSetsWon    (p.getSets()        == null ? 0 : p.getSets());

        /* --- время на площадке --- */
        if (m.getStartedAt() != null && m.getFinishedAt() != null) {
            long secs = Duration.between(m.getStartedAt(), m.getFinishedAt()).getSeconds();
            sp.setTimePlayed(secs / 60f);          // минуты, float
        } else {
            sp.setTimePlayed(null);
        }
        return sp;
    }
}
