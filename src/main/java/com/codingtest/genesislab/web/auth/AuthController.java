package com.codingtest.genesislab.web.auth;

import com.codingtest.genesislab.config.ApiResponse;
import com.codingtest.genesislab.config.SuccessResponse;
import com.codingtest.genesislab.web.auth.in.LoginRequest;
import com.codingtest.genesislab.web.auth.out.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관리")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param loginRequest 로그인 요청 정보
     * @return JWT 토큰이 포함된 응답
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        SuccessResponse<TokenResponse> response = SuccessResponse.of("200 로그인 성공", tokenResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰을 새로 발급받습니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 JWT 토큰이 포함된 응답
     */
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("Refresh-Token") String refreshToken) {
        TokenResponse tokenResponse = authService.refreshToken(accessToken, refreshToken);
        SuccessResponse<TokenResponse> response = SuccessResponse.of("200 토큰 재발급 성공", tokenResponse);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃 처리를 합니다.
     *
     * @return 성공 응답
     */
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("Refresh-Token") String refreshToken) {
        authService.logout(accessToken, refreshToken);
        SuccessResponse<Void> response = SuccessResponse.of("200 로그아웃 성공", null);
        return ResponseEntity.ok(response);
    }

}