package com.drip.competitionengine.bracket;

import com.drip.competitionengine.model.GroupType;
import com.drip.competitionengine.util.ApplicationContextProvider;

import java.util.EnumMap;
import java.util.Map;

public final class BracketStrategies {

    private static final Map<GroupType, BracketStrategy> STRATEGIES =
            new EnumMap<>(GroupType.class);

    static {
        STRATEGIES.put(GroupType.OLYMPIC, ApplicationContextProvider
                .getBean(OlympicStrategy.class));
        STRATEGIES.put(GroupType.ROUND_ROBIN, t -> {
            throw new UnsupportedOperationException("TODO Round-robin");
        });
        STRATEGIES.put(GroupType.SWISS, t -> {
            throw new UnsupportedOperationException("TODO Swiss");
        });
    }

    public static BracketStrategy forType(GroupType type) {
        return STRATEGIES.get(type);
    }

    private BracketStrategies() {}
}
