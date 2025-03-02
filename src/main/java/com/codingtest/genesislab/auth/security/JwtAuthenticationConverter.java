package com.codingtest.genesislab.auth.security;

import com.codingtest.genesislab.auth.token.InvalidateTokenException;
import com.codingtest.genesislab.auth.token.TokenValidator;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * JWT 인증 변환기
 * - JWT 토큰을 Spring Security의 Authentication 객체로 변환하는 책임 처리
 */
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final TokenValidator tokenValidator;

    public JwtAuthenticationConverter(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        if (!tokenValidator.isValid(jwt.getTokenValue())) {
            throw new InvalidateTokenException("토큰 검증이 실패했습니다.");
        }

        String role = getRole(jwt);
        List<GrantedAuthority> authorities = role != null
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                : Collections.emptyList();

        return new JwtAuthenticationToken(jwt, authorities, getUsernameFromJwt(jwt));
    }

    private String getUsernameFromJwt(Jwt jwt) {
        return jwt.getSubject();
    }

    private String getRole(Jwt jwt) {
        return (String) jwt.getClaims().get("role");
    }
}