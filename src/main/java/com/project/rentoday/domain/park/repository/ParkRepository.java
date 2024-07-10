package com.project.rentoday.domain.park.repository;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkRepository extends JpaRepository<Park, Long> {
    List<Park> findByMember(Member member);

    List<Park> findByParkStatus(ParkStatus parkStatus);
    
}
