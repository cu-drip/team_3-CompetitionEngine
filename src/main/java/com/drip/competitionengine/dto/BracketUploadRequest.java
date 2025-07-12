// src/main/java/com/drip/competitionengine/dto/BracketUploadRequest.java
package com.drip.competitionengine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BracketUploadRequest {
    private List<MatchDtoOut> matches;
}
