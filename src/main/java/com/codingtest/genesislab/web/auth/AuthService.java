package com.codingtest.genesislab.web.auth;

import com.codingtest.genesislab.auth.token.RedisAccessTokenBlackList;
import com.codingtest.genesislab.auth.token.TokenService;
import com.codingtest.genesislab.auth.UserAuthenticationService;
import com.codingtest.genesislab.web.auth.out.TokenResponse;
import com.codingtest.genesislab.web.user.out.UserCurrentInfoDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * 인증 처리를 담당하는 서비스
 * - 로그인, 로그아웃, 토큰 갱신 등 인증 관련 비즈니스 로직 처리
 */
@Service
@Transactional
public class AuthService {

    private final TokenService tokenService;
    private final UserAuthenticationService userAuthenticationService;
    private final RedisAccessTokenBlackList redisAccessTokenBlackList;

    public AuthService(TokenService tokenService, UserAuthenticationService userAuthenticationService,
                       RedisAccessTokenBlackList redisAccessTokenBlackList) {
        this.tokenService = tokenService;
        this.userAuthenticationService = userAuthenticationService;
        this.redisAccessTokenBlackList = redisAccessTokenBlackList;
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
     */
    public TokenResponse login(String email, String password) {
        UserCurrentInfoDto dto = userAuthenticationService.authenticateUser(email, password);
        return tokenService.createTokenPair(dto.getId(), email, dto.getRole().getValue());
    }

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰을 발급합니다.
     */
    public TokenResponse refreshToken(String accessToken, String refreshToken) {
        try {
            String pureAccessToken  = makeTokenPure(accessToken);
            String pureRefreshToken = makeTokenPure(refreshToken);

            Long userId = tokenService.getUserIdFromToken(pureRefreshToken);
            redisAccessTokenBlackList.addToBlackList(pureAccessToken, userId, Instant.now());

            UserCurrentInfoDto userInfo = userAuthenticationService.getUserInfoById(userId);
            tokenService.invalidateRefreshToken(refreshToken);

            return tokenService.createTokenPair(userId, userInfo.getEmail(), userInfo.getRole().getValue());

        } catch (Exception e) {
            throw new BadCredentialsException("유효하지 않은 리프레시 토큰입니다.", e);
        }
    }

    /**
     * 사용자 로그아웃을 처리합니다.
     */
    public void logout(String accessToken, String refreshToken) {
        String pureAccessToken  = makeTokenPure(accessToken);
        String pureRefreshToken = makeTokenPure(refreshToken);

        Long userId = tokenService.getUserIdFromToken(pureAccessToken);
        redisAccessTokenBlackList.addToBlackList(pureAccessToken, userId, Instant.now());
        tokenService.invalidateRefreshToken(pureRefreshToken);
    }

    private String makeTokenPure(String token) {
        if (token == null) {
            throw new IllegalArgumentException("토큰이 제공되지 않았습니다.");
        }
        return token.replace("Bearer ", "");
    }
}
