// src/main/java/com/drip/competitionengine/bracket/Bracket.java
package com.drip.competitionengine.bracket;

import com.drip.competitionengine.model.Match;
import java.util.List;

/** DTO для ответа /bracket/{tourId} */
public record Bracket(List<Match> matches) {}
