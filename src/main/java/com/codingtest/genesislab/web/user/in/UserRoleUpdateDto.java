package com.codingtest.genesislab.web.user.in;

import com.codingtest.genesislab.auth.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class UserRoleUpdateDto {

    @NotNull(message = "권한은 필수 입력값입니다.")
    private Role role;

    public UserRoleUpdateDto(Role role) {
        this.role = role;
    }
}