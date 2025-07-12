package com.drip.competitionengine.bracket;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface BracketAware {
    @JsonIgnore
    Bracket getBracket();    // генерирует «на лету»
}
