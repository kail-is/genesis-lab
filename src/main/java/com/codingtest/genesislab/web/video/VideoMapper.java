package com.codingtest.genesislab.web.video;

import com.codingtest.genesislab.domain.Video;
import com.codingtest.genesislab.web.video.in.VideoUploadDto;
import com.codingtest.genesislab.web.video.out.VideoInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    /**
     * Video 엔티티를 VideoInfoDto로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "originalTitle")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "filePath", source = "filePath")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "fileSize", source = "fileSize")
    @Mapping(target = "uploadDate", source = "uploadDate")
    VideoInfoDto toDto(Video video);

}
