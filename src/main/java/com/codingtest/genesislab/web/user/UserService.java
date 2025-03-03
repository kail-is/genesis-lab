package com.codingtest.genesislab.web.user;

import com.codingtest.genesislab.domain.User;
import com.codingtest.genesislab.domain.repository.UserRepository;
import com.codingtest.genesislab.auth.Role;
import com.codingtest.genesislab.web.user.out.UserCurrentInfoDto;
import com.codingtest.genesislab.web.user.in.UserRegisterDto;
import com.codingtest.genesislab.web.user.in.UserUpdateDto;
import com.codingtest.genesislab.web.user.in.UserRoleUpdateDto;
import com.codingtest.genesislab.web.user.in.UserPasswordUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static com.codingtest.genesislab.auth.token.TokenService.getCurrentUser;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * 특정 ID의 사용자를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 삭제된 경우
     */
    @Transactional(readOnly = true)
    public UserCurrentInfoDto getUserInfo(Long userId) {
        User user = findUserByIdAndNotDeleted(userId);
        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 모든 활성 사용자를 조회합니다.
     *
     * @return 사용자 정보 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<UserCurrentInfoDto> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();
        return userMapper.toCurrentInfoDtoList(users);
    }

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param registerDto 사용자 등록 정보
     * @return 등록된 사용자 정보 DTO
     * @throws IllegalArgumentException 이메일이 이미 사용 중이거나 비밀번호가 유효하지 않은 경우
     */
    public UserCurrentInfoDto registerUser(UserRegisterDto registerDto) {

        if (userRepository.existsByEmailAndDeletedFalse(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());

        User user = User.of(
            registerDto.getEmail(),
            encodedPassword,
            registerDto.getName(),
            registerDto.getPhoneNumber(),
            Role.USER
        );

        userRepository.save(user);
        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 사용자의 기본 정보를 업데이트합니다.
     *
     * @param userId 업데이트할 사용자 ID
     * @param updateDto 업데이트 정보
     * @return 업데이트된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 삭제된 경우
     * @throws IllegalArgumentException 이메일이 이미 사용 중인 경우
     */
    public UserCurrentInfoDto updateUser(Long userId, UserUpdateDto updateDto) {
        validateUserPermission(userId);

        User user = findUserByIdAndNotDeleted(userId);
        if (!user.getEmail().equals(updateDto.getEmail()) &&
                userRepository.existsByEmailAndDeletedFalse(updateDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(
            updateDto.getEmail(),
            updateDto.getName(),
            updateDto.getPhoneNumber()
        );

        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 사용자의 권한을 업데이트합니다.
     *
     * @param userId 업데이트할 사용자 ID
     * @param roleUpdateDto 권한 업데이트 정보
     * @return 업데이트된 사용자 정보 DTO
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 삭제된 경우
     */
    public UserCurrentInfoDto updateUserRole(Long userId, UserRoleUpdateDto roleUpdateDto) {
        validateUserPermission(userId);
        User user = findUserByIdAndNotDeleted(userId);
        user.updateRole(roleUpdateDto.getRole());
        return userMapper.toCurrentInfoDto(user);
    }

    /**
     * 사용자의 비밀번호를 업데이트합니다.
     *
     * @param userId 업데이트할 사용자 ID
     * @param passwordUpdateDto 비밀번호 업데이트 정보
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 삭제된 경우
     * @throws IllegalArgumentException 현재 비밀번호가 일치하지 않거나 새 비밀번호가 유효하지 않은 경우
     */
    public void updateUserPassword(Long userId, UserPasswordUpdateDto passwordUpdateDto) {
        validateUserPermission(userId);
        User user = findUserByIdAndNotDeleted(userId);

        if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(passwordUpdateDto.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    /**
     * 사용자를 논리적으로 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     * @throws AccessDeniedException 현재 사용자가 해당 계정을 삭제할 권한이 없는 경우
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 이미 삭제된 경우
     */
    public void deleteUser(Long userId) {
        validateUserPermission(userId);
        findUserByIdAndNotDeleted(userId).delete();
    }

    /**
     * ID를 기반으로 삭제되지 않은 사용자를 찾습니다.
     *
     * @param id 검색할 사용자 ID
     * @return User 엔티티
     * @throws EntityNotFoundException 사용자가 존재하지 않거나 삭제된 경우
     */
    private User findUserByIdAndNotDeleted(Long id) {
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 현재 사용자가 주어진 사용자 ID에 대한 권한이 있는지 검증합니다.
     *
     * @param userId 검증할 사용자 ID
     * @throws AccessDeniedException 권한이 없을 경우 발생
     */
    private void validateUserPermission(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("회원 권한이 없습니다.");
        }
    }

}