package com.codingtest.genesislab.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Role {
    USER,
    ADMIN,
    UNKNOWN;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static Role from(String value) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("권한 값이 잘못 입력되었습니다."));
    }
}
