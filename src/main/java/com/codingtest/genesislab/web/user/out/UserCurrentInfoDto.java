package com.codingtest.genesislab.web.user.out;

import com.codingtest.genesislab.auth.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserCurrentInfoDto {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private Role role;

    @Builder
    public UserCurrentInfoDto(Long id, String email, String name, String phoneNumber, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}

