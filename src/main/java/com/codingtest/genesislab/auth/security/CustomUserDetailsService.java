package com.codingtest.genesislab.auth.security;

import com.codingtest.genesislab.auth.Role;
import com.codingtest.genesislab.auth.UserAuthenticationService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthenticationService userAuthenticationService;

    public CustomUserDetailsService(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 이메일로 역할을 조회
        String role = userAuthenticationService.getUserInfoByEmail(email).getRole().getValue();

        if (role.equals(Role.UNKNOWN.getValue())) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        return User.builder()
                .username(email)
                .password("")
                .roles(role)
                .build();
    }
}
