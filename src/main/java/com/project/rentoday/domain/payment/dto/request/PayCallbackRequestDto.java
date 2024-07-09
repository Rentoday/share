package com.project.rentoday.domain.payment.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PayCallbackRequestDto {
    private String paymentUid;
    private String reservationUid;
}
