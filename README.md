# 제네시스랩 Back-end Engineer 채용 과제

- 지원자: [서은빈](https://artesuh.notion.site/13ce1ea9c27980238bc1c53277f3a647?pvs=4)
- Java 17 / Spring Boot 3.4.3 / Spring Data JPA (MySQL) / Spring Security 6 
- 구동 시 `DDL auto` 설정: 빠른 테스트 가능하시도록 설정해 두었습니다.
- [Swagger](http://localhost:8080/swagger-ui/index.html)를 통한 조회가 가능합니다.
  - `상단 Authorize` 내 `Bearer 없이 토큰 지정` 후 테스트가 가능합니다.

---

# 요구사항 충족 여부 

- **기술 요구사항**
  - Java 17 / Spring Boot 3.4.3 / Spring Data JPA(MySQL) / Spring Security 6 / MySQL / REST 준수
- **DB**
  - User 테이블 생성 - 내부 권한(ROLE) 컬럼 지정 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/domain/User.java#L22)
  - Spring Security로 ROLE 활용 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/auth/token/TokenService.java#L141)
- **기능**
  - **유저 관리**: 회원 가입 / 회원 정보 수정 / 회원 탈퇴 / Email Unique KEY를 통한 유저 식별자 사용
  - **유저 로그인**
    - `Access Token` / `Refresh Token`을 통한 검증 - [링크](https://github.com/kail-is/genesis-lab/blob/main/src/main/java/com/codingtest/genesislab/auth/token/TokenService.java)
    - RDB를 토대로 상태 조회하나 Redis 확장성 고려 (잦은 DB 조회 부하 감소 / SPOF 제거) - [링크](https://github.com/kail-is/genesis-lab/blob/main/src/main/java/com/codingtest/genesislab/auth/token/RedisAccessTokenBlackList.java)
    - Spring Security 내 HS256 암호화 적용 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/config/SecurityConfig.java#L62)
  - **비디오 파일 업로드**
    - user 권한 검증 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/web/video/VideoService.java#L49)
    - 영상 파일 업로드 검증 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/file/FileStorageService.java#L101)
    - 프로젝트 루트 계정 내 /video 디렉토리 저장 (application.properites를 통한 수정 가능)
    - 최대 100mb - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/file/FileStorageService.java#L95)
    - `@PostConstruct`를 통한 로컬 저장소 초기화 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/file/FileStorageService.java#L35C5-L35C19)
  - **비디오 파일 재생**
    - 소유자 / admin 권한 검증 - [링크](https://github.com/kail-is/genesis-lab/blob/cfbad7eccb272b4b05b781971603cf658b0813d7/src/main/java/com/codingtest/genesislab/web/video/VideoService.java#L37)
    - Stream 방식의 206 Progressive Download 구현 
- RequestParams, body 내 `@Valid` 어노테이션을 통한 입력 값 검증 - [링크](https://github.com/kail-is/genesis-lab/blob/main/src/main/java/com/codingtest/genesislab/web/user/in/UserRegisterDto.java)
- **아키텍처**
  - Layered Architecture를 기반으로 Hexagonal / DDD 아이디어 차용 
  - 통신부 web 디렉토리로 분리: in / out DTO를 통해 Port 역할 지정
  - Entity 및 web 내부 domain 별 분리: Domain 위주의 패키지 구조 지정
  - [MapStruct](https://github.com/kail-is/genesis-lab/blob/main/src/main/java/com/codingtest/genesislab/web/user/UserMapper.java)를 활용한 DTO 바인딩 효율화


# API 명세서 

## User - 직원 관리

### 직원 정보 조회
- **GET** `/api/users/{userId}`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (사용자가 존재하지 않는 경우)
- **권한 제한**: 본인 또는 운영자만 조회 가능
- **응답 예시**:
  ```json
  {
    "id": 1,
    "name": "홍길동",
    "email": "hong@example.com",
    "role": "USER"
  }
  ```

### 직원 정보 수정
- **PUT** `/api/users/{userId}`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (유효하지 않은 입력값)
- **권한 제한**: 본인만 수정 가능
- **유효성 검사**:
    - `name`: 필수, 최대 50자
    - `email`: 유효한 이메일 형식
- **요청 예시**:
  ```json
  {
    "name": "김철수",
    "email": "kim@example.com"
  }
  ```

### 직원 탈퇴
- **DELETE** `/api/users/{userId}`
- **성공 코드**: `204 No Content`
- **실패 코드**: `403 Forbidden` (본인만 가능)

### 직원 권한 수정
- **PUT** `/api/users/{userId}/role`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (잘못된 권한 값)
- **권한 제한**: 운영자만 가능
- **유효성 검사**:
    - `role`: `ADMIN` 또는 `USER` 값만 허용

### 직원 비밀번호 수정
- **PUT** `/api/users/{userId}/password`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (비밀번호 형식 오류)
- **권한 제한**: 본인만 가능
- **유효성 검사**:
    - `password`: 최소 8자 이상, 숫자 및 특수문자 포함
- **요청 예시**:
  ```json
  {
    "currentPassword": "OldPass@123",
    "newPassword": "NewPass@123"
  }
  ```

### 모든 직원 조회
- **GET** `/api/users`
- **성공 코드**: `200 OK`
- **실패 코드**: `403 Forbidden` (운영자만 가능)

### 직원 가입
- **POST** `/api/users`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (필수 값 누락)
- **유효성 검사**:
    - `name`: 필수, 최대 50자
    - `email`: 필수, 유효한 이메일 형식
    - `password`: 필수, 최소 8자 이상, 숫자 및 특수문자 포함

## Auth - 인증 관리

> Spring Security 활용 인증 관리 

* Logout / 토큰 재발급 시 기존 Access Token / Refresh Token Revoke 처리
* Access Token: RDB 저장 
  * 로그아웃 등의 토큰 비활성화 대응 위해 저장
  * Redis 조회로 변경하는 추후 확장 고려
* Refresh Token: RDB 저장 
  * 만료 여부 확인 위해 저장

### 토큰 재발급
- **POST** `/api/auth/refresh`
- **성공 코드**: `200 OK`
- **실패 코드**: `401 Unauthorized` (유효하지 않은 리프레시 토큰)
- **요청 예시**:
  ```json
  {
    "refreshToken": "abcd1234"
  }
  ```

### 로그아웃
- **POST** `/api/auth/logout`
- **성공 코드**: `200 OK`
- **실패 코드**: `401 Unauthorized` (인증되지 않은 요청)

### 로그인
- **POST** `/api/auth/login`
- **성공 코드**: `200 OK`
- **실패 코드**: `401 Unauthorized` (잘못된 로그인 정보)
- **유효성 검사**:
    - `email`: 필수, 유효한 이메일 형식
    - `password`: 필수, 최소 8자 이상
- **요청 예시**:
  ```json
  {
    "email": "user@example.com",
    "password": "SecurePass@1"
  }
  ```

## Video - 비디오 관리

### 비디오 업로드
- **POST** `/api/videos`
- **성공 코드**: `200 OK`
- **실패 코드**: `400 Bad Request` (파일 크기 초과, 제목 형식 오류)
- **유효성 검사**:
    - `file`: 필수, 최대 100MB
    - `title`: 필수, 4~20자 사이
- **요청 형식**:
    - `multipart/form-data`
- **요청 예시**:
  ```
  curl -X POST "https://api.example.com/api/videos" \
       -H "Authorization: Bearer token" \
       -F "file=@video.mp4" \
       -F "title=Sample Video"
  ```

### 비디오 스트리밍
- **GET** `/api/videos/{videoId}/stream`
- **성공 코드**: `206 Partial Download`
- **요청 헤더**:
    - `Range`: 부분 다운로드 요청 가능 (예: `bytes=0-999`)
- **응답 헤더**:
    - `Content-Range`: 스트리밍 범위 반환

