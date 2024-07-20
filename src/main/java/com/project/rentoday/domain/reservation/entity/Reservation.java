package com.project.rentoday.domain.reservation.entity;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalTime;
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
    @JoinColumn(name = "park_id")
    private Park park;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @FutureOrPresent
    @Column(name = "check_in", columnDefinition = "TIME")
    private LocalTime checkIn;

    @NotNull
    @Column(name = "check_out", columnDefinition = "TIME")
    private LocalTime checkOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private double amount;

    @Column(name = "reservation_uid")
    private String reservationUid;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Pay pay;

    protected Reservation(Member member, Park park, Long id, LocalTime checkIn, String reservationUid, String reservationName) {

        this.id = id;
    }

    public Reservation(
            Member member,
            Park park,
            LocalTime checkIn,
            double price,
            LocalTime checkOut
    ) {
        this.member = member;
        this.park = park;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.reservationStatus = ReservationStatus.RESERVE;
        this.reservationUid = UUID.randomUUID().toString();
        this.amount = price;
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

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

    public void setStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }
}
