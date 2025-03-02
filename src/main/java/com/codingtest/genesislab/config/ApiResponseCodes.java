package com.codingtest.genesislab.config;

import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiResponseCodes {

    OK("200", HttpStatus.OK, "정상적으로 처리되었습니다."),
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST, "입력 값이 잘못되었습니다."),
    NOT_FOUND("404", HttpStatus.NOT_FOUND, "해당 정보를 찾을 수 없습니다."),
    UNKNOWN("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
    UNAUTHORIZED("401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다.")
    ;

    private final String code;
    private final HttpStatus status;
    private final String reason;
}
