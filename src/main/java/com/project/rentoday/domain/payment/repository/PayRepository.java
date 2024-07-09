package com.project.rentoday.domain.payment.repository;

import com.project.rentoday.domain.payment.entity.Pay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {


}
