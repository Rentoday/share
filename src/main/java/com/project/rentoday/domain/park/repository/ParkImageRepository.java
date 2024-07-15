package com.project.rentoday.domain.park.repository;

import com.project.rentoday.domain.park.entity.ParkImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParkImageRepository extends JpaRepository<ParkImage, Long> {

    @Query("SELECT pi FROM ParkImage pi WHERE pi.park.id = :parkId ORDER BY pi.id ASC LIMIT 1")
    Optional<ParkImage> findFirstByParkId(@Param("parkId") Long parkId);
}
