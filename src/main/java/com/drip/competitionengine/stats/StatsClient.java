// src/main/java/com/drip/competitionengine/stats/StatsClient.java
package com.drip.competitionengine.stats;

import com.drip.competitionengine.dto.StatsPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final WebClient web;            // объявите как @Bean

    @Value("${statistic.url}")
    private String base;

    public void send(StatsPayload payload) {
        web.post()
           .uri(base + "/api/v1/stats/update")
           .bodyValue(payload)
           .retrieve()
           .toBodilessEntity()
           .subscribe();                    // fire-and-forget
    }
}
