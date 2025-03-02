package com.codingtest.genesislab.file;

/**
 * 저장된 파일 정보 클래스
 */
public record StoredFileInfo(
        String fileName,
        String absolutePath,
        String contentType,
        long fileSize
) {}