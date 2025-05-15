package com.codingtest.genesislab.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "VIDEOS")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ORIGINAL_TITLE", nullable = false)
    private String originalTitle;

    @Column(name = "STORED_TITLE", nullable = false)
    private String storedTitle;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "CONTENT_TYPE", nullable = false)
    private String contentType;

    @Column(name = "FILE_SIZE")
    private long fileSize;

    @Column(name = "UPLOAD_DATE", nullable = false)
    private LocalDateTime uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPLOADER_ID", nullable = false)
    private User uploader;

    public Video(String originalTitle, String storedTitle, String description, String filePath,
                 String contentType, long fileSize, LocalDateTime uploadDate, User uploader) {
        this.originalTitle = originalTitle;
        this.storedTitle = storedTitle;
        this.description = description;
        this.filePath = filePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.uploader = uploader;
    }

    public static Video of(String originalTitle, String storedTitle, String description, String filePath,
                           String contentType, long fileSize, LocalDateTime uploadDate, User uploader) {
        return new Video(originalTitle, storedTitle, description, filePath, contentType, fileSize, uploadDate, uploader);
    }
}
