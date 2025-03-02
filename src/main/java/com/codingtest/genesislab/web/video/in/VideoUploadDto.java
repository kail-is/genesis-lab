package com.codingtest.genesislab.web.video.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VideoUploadDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 4, max = 20, message = "제목은 4~20자 사이여야 합니다.")
    private String title;
    private String description;

}
