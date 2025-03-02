package com.codingtest.genesislab.web.video;

import com.codingtest.genesislab.domain.Video;
import com.codingtest.genesislab.domain.repository.VideoRepository;
import com.codingtest.genesislab.file.FileStorageService;
import com.codingtest.genesislab.file.StoredFileInfo;
import com.codingtest.genesislab.web.video.in.VideoUploadDto;
import com.codingtest.genesislab.web.video.out.VideoInfoDto;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoMapper videoMapper;
    private final VideoRepository videoRepository;
    private final FileStorageService fileStorageService;
    private final VideoStreamingService videoStreamingService;

    public ResourceRegion getVideoStream(Long videoId, String rangeHeader) {
        Video video = getVideo(videoId);
        return videoStreamingService.getVideoStream(Paths.get(video.getFilePath()), rangeHeader);
    }

    public VideoInfoDto uploadVideo(MultipartFile file, VideoUploadDto dto) {

        StoredFileInfo storedFileInfo = fileStorageService.store(file);

        Video video = Video.of(
                dto.getTitle(),
                storedFileInfo.fileName(),
                dto.getDescription(),
                storedFileInfo.absolutePath(),
                storedFileInfo.contentType(),
                storedFileInfo.fileSize(),
                LocalDateTime.now()
        );

        return saveVideo(video);
    }

    @Transactional
    public VideoInfoDto saveVideo(Video video) {
        videoRepository.save(video);
        return videoMapper.toDto(video);
    }

    @Transactional(readOnly = true)
    public Video getVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow((() -> new IllegalArgumentException("해당하는 비디오가 없습니다.")));
    }


    public VideoService(VideoMapper videoMapper,
                        VideoRepository videoRepository,
                        VideoStreamingService videoStreamingService,
                        FileStorageService fileStorageService) {
        this.videoMapper = videoMapper;
        this.videoRepository = videoRepository;
        this.videoStreamingService = videoStreamingService;
        this.fileStorageService = fileStorageService;
    }
}