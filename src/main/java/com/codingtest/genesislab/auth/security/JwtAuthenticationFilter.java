package com.codingtest.genesislab.auth.security;

import com.codingtest.genesislab.auth.token.TokenService;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = tokenService.resolveToken(request);

        if (token != null && tokenService.validateToken(token)) {
            tokenService.authenticateUser(token);
        }

        filterChain.doFilter(request, response);
    }

}
