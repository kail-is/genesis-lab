package com.codingtest.genesislab.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends ApiResponse {

    private final String code;
    private final String codeName;
    private final String message;
    private final LocalDateTime time = LocalDateTime.now();
    private final String transactionId = MDC.get("transactionId");

    public ErrorResponse(String code, String codeName, String message) {
        super(code, message);
        this.code = code;
        this.codeName = codeName;
        this.message = message;
    }

    public static ErrorResponse of(final ApiResponseCodes apiResponseCodes) {
        return ErrorResponse.builder()
                .code(apiResponseCodes.getCode())
                .codeName(apiResponseCodes.name())
                .message(apiResponseCodes.getReason())
                .build();
    }

    public static ErrorResponse of(final String message) {
        return ErrorResponse.builder()
                .message(message)
                .build();
    }

    public static ErrorResponse of(final Exception e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    public static ErrorResponse of(final ApiResponseCodes apiResponseCodes, final String message) {
        return ErrorResponse.builder()
                .code(apiResponseCodes.getCode())
                .message(message)
                .build();
    }

}
