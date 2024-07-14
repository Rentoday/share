package com.project.rentoday.domain.park.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkRepository extends JpaRepository<Park, Long> {
    Page<Park> findByMember(Member member, Pageable pageable);

    Page<Park> findByMemberAndParkStatus(Member member, ParkStatus parkStatus, Pageable pageable);

    List<Park> findByParkStatus(ParkStatus parkStatus);
    
}
