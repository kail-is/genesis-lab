package com.codingtest.genesislab.file;

/**
 * 파일 저장소 관련 예외
 */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
