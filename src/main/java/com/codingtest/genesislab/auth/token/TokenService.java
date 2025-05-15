package com.codingtest.genesislab.auth.token;

import com.codingtest.genesislab.auth.UserAuthenticationService;
import com.codingtest.genesislab.domain.User;
import com.codingtest.genesislab.web.auth.out.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * JWT 토큰 생성 및 검증을 담당하는 서비스
 */
@Service
@Transactional
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserAuthenticationService userAuthenticationService;
    private final RefreshTokenService refreshTokenService;
    private final RedisAccessTokenBlackList redisAccessTokenBlackList;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder,
                        UserAuthenticationService userAuthenticationService, RefreshTokenService refreshTokenService,
                        RedisAccessTokenBlackList redisAccessTokenBlackList) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userAuthenticationService = userAuthenticationService;
        this.refreshTokenService = refreshTokenService;
        this.redisAccessTokenBlackList = redisAccessTokenBlackList;
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            jwtDecoder.decode(token);
            if (redisAccessTokenBlackList.isBlacklisted(token)) {
                return false;
            }
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("userId");
    }

    public String getEmailFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("sub");
    }

    public String getRoleFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("role");
    }

    public String createAccessToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("genesislab")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600L))
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").keyId("genesislab-key").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken(Long userId) {

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("genesislab")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(604800L))
                .claim("userId", userId)
                .build();
        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").keyId("genesislab-key").build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        Instant expiresAt = now.plusSeconds(604800L);
        refreshTokenService.createRefreshToken(refreshToken, userId, expiresAt);

        return refreshToken;
    }

    public Long validateRefreshToken(String refreshToken) {
        try {
            jwtDecoder.decode(refreshToken);
            return refreshTokenService.validateToken(refreshToken);
        } catch (JwtException e) {
            return null;
        }
    }

    public void invalidateRefreshToken(String token) {
        refreshTokenService.invalidateToken(token);
    }

    public TokenResponse createTokenPair(Long userId, String email, String role) {
        String accessToken = createAccessToken(userId, email, role);
        String refreshToken = createRefreshToken(userId);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600L)
                .build();
    }

    public void authenticateUser(String token) {
        String email = getEmailFromToken(token);
        User user = userAuthenticationService.getUserByEmail(email);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, authorities)
        );
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new RuntimeException("유효하지 않은 사용자 정보입니다.");
        }
    }

}
