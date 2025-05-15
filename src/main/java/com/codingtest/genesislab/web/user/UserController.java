package com.codingtest.genesislab.web.user;

import com.codingtest.genesislab.config.ApiResponse;
import com.codingtest.genesislab.config.SuccessResponse;
import com.codingtest.genesislab.web.user.in.UserPasswordUpdateDto;
import com.codingtest.genesislab.web.user.in.UserRoleUpdateDto;
import com.codingtest.genesislab.web.user.out.UserCurrentInfoDto;
import com.codingtest.genesislab.web.user.in.UserRegisterDto;
import com.codingtest.genesislab.web.user.in.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "직원 관리")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 직원 가입을 받습니다.
     *
     * @param registerDto 가입 정보
     * @return 등록된 직원 정보를 포함하는 ResponseEntity
     */
    @Operation(summary = "직원 가입", description = "직원 가입을 받습니다.")
    @PostMapping("")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid UserRegisterDto registerDto) {

        UserCurrentInfoDto userInfo = userService.registerUser(registerDto);
        SuccessResponse<UserCurrentInfoDto> successResponse = SuccessResponse.of("200 가입 완료", userInfo);

        return ResponseEntity.ok(successResponse);
    }

    /**
     * 특정 직원의 정보를 조회합니다.
     *
     * @param userId 직원의 ID
     * @return 직원의 정보를 포함하는 ResponseEntity
     */
    @Operation(summary = "직원 정보 조회", description = "직원의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserInfo(
            @PathVariable @Positive(message = "직원 ID는 양수여야 합니다.") Long userId) {

        UserCurrentInfoDto userInfo = userService.getUserInfo(userId);
        SuccessResponse<UserCurrentInfoDto> successResponse = SuccessResponse.of("200 조회 완료", userInfo);

        return ResponseEntity.ok(successResponse);
    }

    /**
     * 모든 직원의 정보를 조회합니다.
     *
     * @return 모든 직원 정보 목록을 포함하는 ResponseEntity
     */
    @Operation(summary = "모든 직원 조회", description = "모든 직원 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserCurrentInfoDto> users = userService.getAllUsers();
        SuccessResponse<List<UserCurrentInfoDto>> successResponse = SuccessResponse.of("200 조회 완료", users);

        return ResponseEntity.ok(successResponse);
    }


    /**
     * 특정 직원의 정보를 업데이트합니다.
     *
     * @param userId 직원의 ID
     * @param updateDto 업데이트할 직원 정보
     * @return 성공 응답
     */
    @Operation(summary = "직원 정보 수정", description = "직원의 정보를 업데이트합니다.")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable @Positive(message = "직원 ID는 양수여야 합니다.") Long userId,
            @RequestBody @Valid UserUpdateDto updateDto) {

        UserCurrentInfoDto updatedUser = userService.updateUser(userId, updateDto);
        SuccessResponse<UserCurrentInfoDto> res = SuccessResponse.of("200 권한 업데이트 완료", updatedUser);

        return ResponseEntity.ok(res);
    }


    /**
     * 특정 직원의 권한을 업데이트합니다.
     *
     * @param userId 직원의 ID
     * @param roleUpdateDto 업데이트할 권한 정보
     * @return 업데이트된 직원 정보를 포함하는 ResponseEntity
     */
    @Operation(summary = "직원 권한 수정", description = "직원의 권한을 업데이트합니다.")
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable @Positive(message = "직원 ID는 양수여야 합니다.") Long userId,
            @RequestBody @Valid UserRoleUpdateDto roleUpdateDto) {

        UserCurrentInfoDto updatedUser = userService.updateUserRole(userId, roleUpdateDto);
        SuccessResponse<UserCurrentInfoDto> res = SuccessResponse.of("200 권한 업데이트 완료", updatedUser);

        return ResponseEntity.ok(res);
    }


    /**
     * 특정 직원의 비밀번호를 업데이트합니다.
     *
     * @param userId 직원의 ID
     * @param passwordUpdateDto 업데이트할 비밀번호 정보
     * @return 성공 응답
     */
    @Operation(summary = "직원 비밀번호 수정", description = "직원의 비밀번호를 업데이트합니다.")
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @PathVariable @Positive(message = "직원 ID는 양수여야 합니다.") Long userId,
            @RequestBody @Valid UserPasswordUpdateDto passwordUpdateDto) {

        userService.updateUserPassword(userId, passwordUpdateDto);
        SuccessResponse<String> res = SuccessResponse.of("200 비밀번호 변경 완료");

        return ResponseEntity.ok(res);
    }

    /**
     * 특정 직원을 삭제합니다.
     *
     * @param userId 직원의 ID
     * @return 성공 응답
     */
    @Operation(summary = "직원 탈퇴", description = "직원을 삭제합니다. (논리적 삭제)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable @Positive(message = "직원 ID는 양수여야 합니다.") Long userId) {

        userService.deleteUser(userId);
        SuccessResponse<String> res = SuccessResponse.of("200 삭제 완료");

        return ResponseEntity.ok(res);
    }

}