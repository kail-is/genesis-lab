package com.codingtest.genesislab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "REFRESH_TOKENS")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ISSUED_AT", nullable = false)
    private Instant issuedAt;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Instant expiresAt;

    @Column(name = "REVOKED", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "REPLACED_BY", length = 500)
    private String replacedBy;

    public boolean setRevoked() {
        return revoked = true;
    }
}