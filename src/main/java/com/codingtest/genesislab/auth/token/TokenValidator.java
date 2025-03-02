package com.codingtest.genesislab.auth.token;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 토큰 유효성 검증을 담당하는 클래스
 * - 토큰의 구문 분석, 서명, 블랙리스트 확인 등 검증 작업 책임 처리
 */
@Component
public class TokenValidator {

    private final JwtDecoder jwtDecoder;
    private final RedisAccessTokenBlackList redisAccessTokenBlackList;

    public TokenValidator(JwtDecoder jwtDecoder, RedisAccessTokenBlackList redisAccessTokenBlackList) {
        this.jwtDecoder = jwtDecoder;
        this.redisAccessTokenBlackList = redisAccessTokenBlackList;
    }

    /**
     * 토큰의 유효성 검증
     *
     * @param token 검증할 토큰
     * @return 유효하면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public boolean isValid(String token) {
        try {

            jwtDecoder.decode(token);
            return !redisAccessTokenBlackList.isBlacklisted(token);

        } catch (RuntimeException e) {
            throw new InvalidateTokenException("토큰 검증이 실패했습니다.");
        }
    }
}