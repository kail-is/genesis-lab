package com.codingtest.genesislab.auth.token;

import com.codingtest.genesislab.domain.RefreshToken;

import com.codingtest.genesislab.domain.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 리프레시 토큰을 생성하고 저장합니다.
     */
    @Transactional
    public void createRefreshToken(String token, Long userId, Instant expiresAt) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .issuedAt(Instant.now())
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 토큰이 유효한지 확인하고 해당 사용자 ID를 반환합니다.
     * 토큰이 유효하지 않으면 null을 반환합니다.
     */
    @Transactional(readOnly = true)
    public Long validateToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();

            if (!refreshToken.isRevoked() && refreshToken.getExpiresAt().isAfter(Instant.now())) {
                return refreshToken.getUserId();
            }
        }

        return null;
    }

    /**
     * 토큰을 무효화합니다.
     */
    @Transactional
    public void invalidateToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        refreshTokenOpt.ifPresent(rt -> {
            rt.setRevoked();
            refreshTokenRepository.save(rt);
        });
    }
}