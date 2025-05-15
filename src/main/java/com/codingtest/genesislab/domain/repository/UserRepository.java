package com.codingtest.genesislab.domain.repository;

import com.codingtest.genesislab.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ID로 논리적 삭제되지 않은 사용자 조회
     */
    Optional<User> findByIdAndDeletedFalse(Long id);

    /**
     * 이메일로 논리적 삭제되지 않은 사용자 조회
     */
    Optional<User> findByEmailAndDeletedFalse(String email);

    /**
     * 이메일이 존재하고 논리적 삭제되지 않은 사용자 확인
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * 논리적 삭제되지 않은 사용자 전체 조회
     */
    List<User> findAllByDeletedFalse();
}