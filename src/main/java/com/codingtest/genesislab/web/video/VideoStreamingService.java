package com.codingtest.genesislab.web.video;

import com.codingtest.genesislab.domain.Video;
import com.codingtest.genesislab.domain.repository.VideoRepository;
import com.codingtest.genesislab.web.video.in.VideoUploadDto;
import com.codingtest.genesislab.web.video.out.VideoInfoDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
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
import java.util.UUID;

@Service
public class VideoStreamingService {

    public ResourceRegion getVideoStream(Path videoPath, String rangeHeader) {
        try {

            if (!exists(videoPath)) {
                throw new FileNotFoundException("비디오 파일을 찾을 수 없습니다.");
            }

            long fileSize = Files.size(videoPath);

            // 비디오 리소스 생성
            Resource videoResource = new UrlResource(videoPath.toUri());

            ResourceRegion region = new ResourceRegion(videoResource, 0, fileSize);

            // Range 헤더가 있고 유효한 범위가 있을 때만 부분 리소스
            if (rangeHeader != null && !rangeHeader.isEmpty()) {
                List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
                if (!ranges.isEmpty()) {
                    HttpRange range = ranges.get(0);
                    long start = range.getRangeStart(fileSize);
                    long end = range.getRangeEnd(fileSize);
                    long rangeLength = end - start + 1;

                    region = new ResourceRegion(videoResource, start, rangeLength);
                }
            }

            return region;
        } catch (IOException e) {
            throw new RuntimeException("비디오 스트리밍 중 오류 발생", e);
        }
    }



    /**
     * 파일 존재 여부 확인
     */
    public boolean exists(Path path) {
        return Files.exists(path);
    }

}