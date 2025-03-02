package com.codingtest.genesislab.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> extends ApiResponse {
    private final String message;
    private final T data;
    private final LocalDateTime time = LocalDateTime.now();
    private final String transactionId = MDC.get("transactionId");

    public SuccessResponse(String message, T data) {
        super("200", message);
        this.message = message;
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(message, data);
    }

    public static SuccessResponse<String> of(String message) {
        return new SuccessResponse<>(message, null);
    }
}