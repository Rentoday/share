package com.project.rentoday.domain.reservation.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.reservationUid = :uid")
    Optional<Reservation> findByReservationUid(@Param("uid") String uid);


    boolean isTimeSlotOverlapping(Park park, LocalTime checkIn, LocalTime checkOut);


    @Query("SELECT r.checkIn, r.checkOut FROM Reservation r WHERE r.park.id = :parkId")
    List<LocalTime[]> findReservationTimesByParkId(@Param("parkId") Long parkId);

    Page<Reservation> findByMember(Member member, Pageable pageable);

    Page<Reservation> findByMemberAndPayIsNotNull(Member member, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.reservationUid = :reservationUid")
    Optional<Reservation> findReservationAndPayAndMember(@Param("reservationUid") String reservationUid);

    @Query("SELECT r FROM Reservation r WHERE r.reservationUid = :reservationUid")
    Optional<Reservation> findReservationAndPay(@Param("reservationUid") String reservationUid);

    List<Reservation> findByParkId(Long parkId);

}
