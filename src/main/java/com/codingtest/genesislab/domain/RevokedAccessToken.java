package com.codingtest.genesislab.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "REVOKED_ACCESS_TOKENS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevokedAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN_IDENTIFIER", nullable = false, unique = true, length = 100)
    private String tokenIdentifier;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "REVOKED_AT", nullable = false)
    private Instant revokedAt;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Instant expiresAt;

}
