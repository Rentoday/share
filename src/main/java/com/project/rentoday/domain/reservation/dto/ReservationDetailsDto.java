package com.project.rentoday.domain.reservation.dto;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ReservationDetailsDto {
    private String reservationUid;
    private String parkingNum;
    private LocalTime startTime;
    private LocalTime endTime;
    private double price;
    private String agency;
    private String agencyPhone;
    private String description;
    private String buyerEmail;
    private String buyerName;

}
