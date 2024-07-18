package com.project.rentoday.domain.park.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkDetailsDto {
    private Long parkId;
    private String parkingNum;
    private LocalTime parkStartTime;
    private LocalTime parkEndTime;
    private double price;
    private String agency;
    private String agencyPhone;
    private String description;
    private String reservationUid;
    private LocalTime reservationStartTime;
    private LocalTime reservationEndTime;
    private String buyerEmail;
    private String buyerName;
}
