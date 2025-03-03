package com.codingtest.genesislab.web.video;

import com.codingtest.genesislab.auth.Role;
import com.codingtest.genesislab.domain.User;
import com.codingtest.genesislab.domain.Video;
import com.codingtest.genesislab.domain.repository.VideoRepository;
import com.codingtest.genesislab.file.FileStorageService;
import com.codingtest.genesislab.file.StoredFileInfo;
import com.codingtest.genesislab.web.video.in.VideoUploadDto;
import com.codingtest.genesislab.web.video.out.VideoInfoDto;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.codingtest.genesislab.auth.token.TokenService.getCurrentUser;

@Service
public class VideoService {

    private final VideoMapper videoMapper;
    private final VideoRepository videoRepository;
    private final FileStorageService fileStorageService;
    private final VideoStreamingService videoStreamingService;


    /**
     * 동영상 스트리밍 요청 처리
     */
    public ResourceRegion getVideoStream(Long videoId, String rangeHeader) {
        Video video = getVideo(videoId);
        User currentUser = getCurrentUser();

        if (!isOwnerOrAdmin(video, currentUser)) {
            throw new SecurityException("해당 비디오에 접근할 권한이 없습니다.");
        }

        return videoStreamingService.getVideoStream(Paths.get(video.getFilePath()), rangeHeader);
    }

    /**
     * 비디오 업로드
     */
    public VideoInfoDto uploadVideo(MultipartFile file, VideoUploadDto dto) {
        User currentUser = getCurrentUser();

        if (!currentUser.getRole().equals(Role.USER)) {
            throw new IllegalArgumentException("비디오는 USER 회원만이 등록 가능합니다.");
        }

        StoredFileInfo storedFileInfo = fileStorageService.store(file);

        Video video = Video.of(
                dto.getTitle(),
                storedFileInfo.fileName(),
                dto.getDescription(),
                storedFileInfo.absolutePath(),
                storedFileInfo.contentType(),
                storedFileInfo.fileSize(),
                LocalDateTime.now(),
                currentUser
        );

        return saveVideo(video);
    }

    /**
     * 비디오 저장
     */
    @Transactional
    public VideoInfoDto saveVideo(Video video) {
        videoRepository.save(video);
        return videoMapper.toDto(video);
    }

    /**
     * 비디오 조회
     */
    @Transactional(readOnly = true)
    public Video getVideo(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 비디오가 없습니다."));
    }

    /**
     * 현재 사용자가 비디오 소유자이거나 관리자 권한인지 확인
     */
    private boolean isOwnerOrAdmin(Video video, User user) {
        return video.getUploader().equals(user) || user.getRole().equals(Role.ADMIN);
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
