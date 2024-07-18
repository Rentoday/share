package com.project.rentoday.domain.park.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationResponse {
    private boolean isValid;
}
