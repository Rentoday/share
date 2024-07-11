package com.project.rentoday.global.jwt.repository;

import com.project.rentoday.global.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refresh);

    //token 존재 유무
    Boolean existsByRefreshToken(String refresh);

    //token 삭제
    void deleteByRefreshToken(String refresh);
}

