package com.drip.competitionengine.mapper;

import com.drip.competitionengine.dto.*;
import com.drip.competitionengine.model.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    /* ---------- entity → DTO ---------- */

    @Mappings({
        @Mapping(target = "matchId",      source = "id"),
        @Mapping(target = "tournamentId", source = "tournament.id")
    })
    MatchDtoOut toDto(Match match);

    List<MatchDtoOut> toDto(List<Match> list);

    /* ---------- DTO → entity ---------- */

    @InheritInverseConfiguration
    Match toEntity(MatchDtoOut dto);

    /* ---------- partial patch ---------- */

    void patch(@MappingTarget Match target, MatchDtoOut dto);

    /* ---------- nested mapping: participants ---------- */

    MatchParticipantDto toDto(MatchParticipant mp);
    List<MatchParticipantDto> toDtoParticipants(List<MatchParticipant> mps);

    @InheritInverseConfiguration
    MatchParticipant toEntity(MatchParticipantDto dto);
}
