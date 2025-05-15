package com.codingtest.genesislab.auth.token;

public class InvalidateTokenException extends RuntimeException {
    public InvalidateTokenException(String message) {
        super(message);
    }
}

