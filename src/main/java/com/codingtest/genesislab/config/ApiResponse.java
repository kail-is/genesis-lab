package com.codingtest.genesislab.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApiResponse {

    private String code;
    private String message;

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
