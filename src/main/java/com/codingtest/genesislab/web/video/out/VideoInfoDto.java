package com.codingtest.genesislab.web.video.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfoDto {
    private Long id;
    private String title;
    private String description;
    private String filePath;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadDate;
}