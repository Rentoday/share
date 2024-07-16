package com.project.rentoday.domain.reservation.entity;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.domain.reservation.converter.DurationConverter;
import com.project.rentoday.global.entity.BaseEntity;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "reservation")
@ToString
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_id", nullable = false)
    private Long id;

    @ManyToOne(targetEntity = Park.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pa_id")
    private Park park;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @FutureOrPresent
    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @NotNull
    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Convert(converter = DurationConverter.class)
    @Column(name = "rental_duration")
    private Duration rentalDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private double amount;

    @Column(name = "reservation_uid")
    private String reservationUid;

    @Column(name = "name", nullable = false)
    private String reservationName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Pay pay;

    protected Reservation(Long id) {

        this.id = id;
    }

    public Reservation(
            Member member,
            Park park,
            Pay pay,
            LocalDateTime checkIn,
            String reservationUid,
            String reservationName
    ) {
        this.member = member;
        this.park = park;
        this.pay = pay;
        this.checkIn = checkIn;
        this.checkOut = calculateCheckOut(checkIn);
        this.reservationStatus = ReservationStatus.RESERVE;
        this.reservationUid = reservationUid;
        this.reservationName = reservationName;
        this.rentalDuration = calculateRentalDuration(checkIn, checkOut);
        this.amount = calculateAmount(park);

        validate();
    }


    //Reservation의 엔티티 불변성 유지
    public void validate() {
        //1. checkIn시간이 check-out보다 나중인 경우 예외를 던집니다.
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("체크인 시간은 체크아웃 시간보다 이전에 있어야 합니다.");
        }
        //2. amount가 음수일 경우 예외를 던집니다.
        if (amount < 0) {
            throw new IllegalArgumentException("총 금액은 양수여야 합니다.");
        }

        //3. reserveNumber 값이 비어 있는 경우 예외를 던집니다.
        if (reservationUid == null || reservationUid.isEmpty()) {
            throw new IllegalArgumentException("예약번호는 항상 있어야 합니다.");
        }
    }
    //checkIn 시간부터 checkOut 시간까지 계산하기.
    public Duration calculateRentalDuration(LocalDateTime checkIn, LocalDateTime checkOut) {
        return Duration.between(checkIn, checkOut);
    }

    //체크인 시간에 따른 체크아웃 계산하기, 시간당으로 계산하기.
    public LocalDateTime calculateCheckOut(LocalDateTime checkIn) {
        return checkIn.plusMinutes(59);
    }

    //주차 요금 조회
    public Double calculateAmount(Park park) {
        double price = park.getPrice();
        long hours = rentalDuration.toHours();
        return hours * price;
    }
    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

    public void setStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }
}
