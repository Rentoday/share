package com.project.rentoday.domain.payment.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PayRequestDto {

    private String reservationUid;
    private String reservationName;
    private String buyerName;
    private double price;
    private String buyerEmail;

    @Builder
    public PayRequestDto(String reservationUid, String reservationName, String buyerName, double price, String buyerEmail) {
        this.reservationUid = reservationUid;
        this.reservationName = reservationName;
        this.buyerName = buyerName;
        this.price = price;
        this.buyerEmail = buyerEmail;
    }
}
