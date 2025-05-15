package com.codingtest.genesislab.auth;

import com.codingtest.genesislab.domain.User;
import com.codingtest.genesislab.domain.repository.UserRepository;
import com.codingtest.genesislab.web.user.UserMapper;
import com.codingtest.genesislab.web.user.out.UserCurrentInfoDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증 및 정보 조회를 담당하는 서비스.
 */
@Service
public class UserAuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 생성자에서 필요한 의존성을 주입받는다.
     */
    public UserAuthenticationService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자를 인증하고, 인증된 사용자 정보를 반환한다.
     */
    public UserCurrentInfoDto authenticateUser(String email, String password) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 이메일입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("유효하지 않은 비밀번호입니다.");
        }

        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 사용자 ID로 사용자 정보를 조회한다.
     */
    public UserCurrentInfoDto getUserInfoById(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 사용자입니다."));
        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 사용자 ID로 사용자 엔티티를 조회한다.
     */
    public User getUserByUserId(Long userId) {
        return userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 사용자입니다."));
    }

    /**
     * 이메일로 사용자 정보를 조회한다.
     */
    public UserCurrentInfoDto getUserInfoByEmail(String email) {
        User user = getUserByEmail(email);
        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 이메일로 사용자 엔티티를 조회한다.
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BadCredentialsException("유효하지 않은 이메일입니다."));
    }

}
