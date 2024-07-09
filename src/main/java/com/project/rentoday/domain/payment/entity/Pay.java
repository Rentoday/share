package com.project.rentoday.domain.payment.entity;

import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "payment_id", nullable = false)
    private String paymentUid;

    @OneToOne(targetEntity = Reservation.class, fetch = FetchType.LAZY)
    private Reservation reservation;

    public Pay(double amount, PaymentStatus status) {
        this.amount = amount;
        this.paymentStatus = status;
    }

    public void changePayBySuccess(PaymentStatus status, String paymentUid) {
        this.paymentStatus = status;
        this.paymentUid = paymentUid;
    }
}
