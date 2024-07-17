package com.project.rentoday.domain.reservation.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequestDto {

    //주차 아이디
    private Long parkId;

    //멤버 아이디
    private Long memberId;

    //시간
    private LocalTime checkIn;

    //결제
    private Long paymentId;

    private String reservationUid;

    private String reservationName;
}
