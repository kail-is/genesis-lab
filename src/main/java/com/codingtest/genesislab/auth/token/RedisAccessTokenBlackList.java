package com.codingtest.genesislab.auth.token;

import com.codingtest.genesislab.domain.RevokedAccessToken;
import com.codingtest.genesislab.domain.repository.RevokedAccessTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Redis를 사용함이 나았으나 RDB로 구현한 액세스 토큰 블랙리스트
 * 후에 Redis로 마이그레이션할 때 인터페이스만 유지하고 구현체를 변경하면 됨
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAccessTokenBlackList {

    private final RevokedAccessTokenRepository revokedTokenRepository;

    /**
     * 토큰을 블랙리스트에 추가합니다.
     */
    @Transactional
    public void addToBlackList(String token, Long userId, Instant expiresAt) {
        String tokenIdentifier = generateTokenIdentifier(token);

        RevokedAccessToken revokedToken = RevokedAccessToken.builder()
                .tokenIdentifier(tokenIdentifier)
                .userId(userId)
                .revokedAt(Instant.now())
                .expiresAt(expiresAt)
                .build();

        revokedTokenRepository.save(revokedToken);
        log.debug("액세스 토큰 블랙리스트에 추가: {}", userId);
    }

    
    /**
     * 토큰이 블랙리스트에 있는지 확인합니다.
     */
    @Transactional(readOnly = true)
    public boolean isBlacklisted(String token) {
        String tokenIdentifier = generateTokenIdentifier(token);
        return revokedTokenRepository.existsBytokenIdentifier(tokenIdentifier);
    }

    /**
     * 만료된 토큰을 정리합니다. 정기적으로 실행되는 스케줄러에서 호출합니다.
     */
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        revokedTokenRepository.deleteExpiredTokens(now);
        log.info("Cleaned up expired tokens from blacklist");
    }

    /**
     * JWT 토큰으로부터 고유 식별자를 생성합니다.
     */
    public String generateTokenIdentifier(String token) {
        String[] parts = token.split("\\.");
        if (parts.length >= 2) {
            return parts[2];
        }
        return Base64.getEncoder().encodeToString(token.getBytes());
    }
}