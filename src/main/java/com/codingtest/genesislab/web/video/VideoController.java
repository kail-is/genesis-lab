package com.codingtest.genesislab.web.video;

import com.codingtest.genesislab.config.ApiResponse;
import com.codingtest.genesislab.config.SuccessResponse;
import com.codingtest.genesislab.web.video.out.VideoInfoDto;
import com.codingtest.genesislab.web.video.in.VideoUploadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Video", description = "비디오 관리")
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 비디오를 업로드합니다.
     *
     * @param videoFile 업로드할 비디오 파일
     * @param videoUploadDto 비디오 업로드 데이터
     * @return 업로드된 비디오 정보를 포함하는 ResponseEntity
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    @Operation(summary = "비디오 업로드", description = "새로운 비디오를 업로드합니다")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse> uploadVideo(
            @RequestPart("videoFile") MultipartFile videoFile,
            @RequestPart("metadata") @Valid VideoUploadDto videoUploadDto) {

        VideoInfoDto videoDTO = videoService.uploadVideo(videoFile, videoUploadDto);
        SuccessResponse<VideoInfoDto> successResponse = SuccessResponse.of("200 업로드 완료", videoDTO);
        return ResponseEntity.ok(successResponse);
    }

    /**
     * 비디오를 스트리밍합니다.
     *
     * @param videoId 스트리밍할 비디오 ID
     * @param rangeHeader Range 헤더
     * @return 비디오 스트림 리소스를 포함하는 ResponseEntity
     */
    @Operation(summary = "비디오 스트리밍", description = "비디오를 스트리밍합니다")
    @Parameter(name = "videoId", description = "비디오 ID")
    @GetMapping("/{videoId}/stream")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable @Positive(message = "비디오 ID는 양수여야 합니다.") Long videoId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        ResourceRegion resourceRegion = videoService.getVideoStream(videoId, rangeHeader);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(resourceRegion.getResource())
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }
}
