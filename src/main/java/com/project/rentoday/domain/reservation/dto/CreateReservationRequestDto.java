package com.project.rentoday.domain.reservation.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateReservationRequestDto {

    private String email;
    //주차 아이디
    private Long parkId;

    //시간
    private List<LocalTime> checkInTimes;

    //총 금액
    private double estimatedPrice;


}
