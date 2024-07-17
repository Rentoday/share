package com.project.rentoday.domain.reservation.dto;

import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ReservationDetailsDto {
    private String reservationUid;
    private String reservationName;
    private String parkingNum;
    private String startTime;
    private String endTime;
    private double price;
    private String agency;
    private String agencyPhone;
    private String description;
    private String imageUrl;
    private int duration;
    private double totalPrice;
    private String buyerEmail;
    private String buyerName;

}