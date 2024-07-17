package com.project.rentoday.domain.payment.dto.response;

import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PayInfoResponse {

    private Long id;
    private String parkNum;
    private String address;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private double amount;
    private LocalDateTime saleDate;
    private PaymentStatus paymentStatus;

    public PayInfoResponse(Long id, Pay pay) {
        this.id = id;
        this.parkNum = pay.getReservation().getPark().getParkingNum();
        this.address = pay.getReservation().getPark().getAddress();
        this.checkIn = pay.getReservation().getCheckIn();
        this.checkOut = pay.getReservation().getCheckOut();
        this.amount = pay.getAmount();
        this.saleDate = pay.getCreatedDate();
        this.paymentStatus = pay.getStatus();
    }
}
