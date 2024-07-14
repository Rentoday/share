package com.project.rentoday.domain.payment.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.payment.entity.Pay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {

    @Query("SELECT p FROM Pay p WHERE p.reservation.member.id = :memberId")
    List<Pay> findByMemberId(@Param("memberId") Long memberId);
}
