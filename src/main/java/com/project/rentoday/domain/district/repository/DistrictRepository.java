package com.project.rentoday.domain.district.repository;

import com.project.rentoday.domain.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByName(String address);
}
