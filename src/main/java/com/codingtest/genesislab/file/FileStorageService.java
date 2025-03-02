package com.codingtest.genesislab.file;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 파일 저장
 */
@Service
public class FileStorageService {

    @Value("${video.storage.location}")
    private String storageLocation;

    private Path rootLocation;

    private final long fileUploadMaxSize;

    public FileStorageService(@Value("${video.max.upload.size:104857600}") long fileUploadMaxSize) {
        this.fileUploadMaxSize = fileUploadMaxSize;
    }

    /**
     * 저장소 초기화
     */
    @PostConstruct
    public void init() {
        rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("저장소 초기화 실패", e);
        }
    }

    /**
     * 파일 저장 및 신규 생성된 파일 경로 반환
     *
     * @param file 저장할 파일
     * @param directory 지정 디렉토리 (null인 경우 기본 rootLocation 사용)
     * @return 저장된 파일의 전체 경로
     */
    public StoredFileInfo store(MultipartFile file, String directory) {
        validateFile(file);

        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String newFileName = UUID.randomUUID() + "." + fileExtension;

            Path targetLocation = directory != null
                    ? Paths.get(directory).resolve(newFileName)
                    : rootLocation.resolve(newFileName);

            Path absolutePath = targetLocation.normalize().toAbsolutePath();
            Files.createDirectories(absolutePath.getParent());
            Files.copy(file.getInputStream(), absolutePath);

            return new StoredFileInfo(
                    newFileName,
                    absolutePath.toString(),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new StorageException("파일 저장 실패", e);
        }
    }

    /**
     * 루트 디렉토리에 파일 저장
     */
    public StoredFileInfo store(MultipartFile file) {
        return store(file, null);
    }

    /**
     * 파일 유효성
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("파일이 비어있습니다.");
        }

        if (file.getSize() > fileUploadMaxSize) {
            throw new MaxUploadSizeExceededException(fileUploadMaxSize);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        if (!isVideoFile(fileExtension)) {
            throw new StorageException("영상 파일만 업로드할 수 있습니다.");
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 영상 확장자 검증
     */
    private boolean isVideoFile(String extension) {
        extension = extension.toLowerCase();
        return extension.equals("mp4") ||
                extension.equals("avi") ||
                extension.equals("mov") ||
                extension.equals("wmv") ||
                extension.equals("flv") ||
                extension.equals("mkv") ||
                extension.equals("webm");
    }


}
