package com.drip.competitionengine.bracket;

import com.drip.competitionengine.model.Tournament;

@FunctionalInterface
public interface BracketStrategy {
    Bracket generate(Tournament tour);
}
