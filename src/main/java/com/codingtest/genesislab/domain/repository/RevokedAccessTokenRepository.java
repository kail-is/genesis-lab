package com.codingtest.genesislab.domain.repository;

import com.codingtest.genesislab.domain.RevokedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RevokedAccessTokenRepository extends JpaRepository<RevokedAccessToken, Long> {

    /**
     * 토큰이 블랙리스트에 존재하는지 확인
     */
    boolean existsBytokenIdentifier(String tokenIdentifier);

    /**
     * 만료된 토큰을 삭제하는 메서드
     * (스케줄러 작업이나 배치 작업으로 주기적으로 호출)
     */
    @Modifying
    @Query("DELETE FROM RevokedAccessToken t WHERE t.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
}