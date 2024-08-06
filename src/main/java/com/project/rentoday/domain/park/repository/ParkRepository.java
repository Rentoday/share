package com.project.rentoday.domain.park.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ParkRepository extends JpaRepository<Park, Long> {
    Page<Park> findByMember(Member member, Pageable pageable);

    Page<Park> findByMemberAndParkStatus(Member member, ParkStatus parkStatus, Pageable pageable);

    Page<Park> findByParkStatus(ParkStatus status, Pageable pageable);

    @Query("SELECT p FROM Park p WHERE LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%')) " +
            "AND ((:searchTime BETWEEN p.startTime AND p.endTime) " +
            "OR (p.startTime > p.endTime AND (:searchTime >= p.startTime OR :searchTime <= p.endTime)))")
    List<Park> findAvailableParks(@Param("address") String address, @Param("searchTime") LocalTime searchTime);



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Park p WHERE p.id = :id")
    Optional<Park> findByIdWithLock(@Param("id") Long id);
}
