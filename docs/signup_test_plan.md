# 회원가입 기능 테스트 계획


## 회원가입 기능 테스트 계획

1. **성공적인 회원가입 테스트**
    request
   {
   "userId": "testuser",
   "username": "John Doe",
   "password": "password1234",
   "email": "john.doe@example.com",
   "gender": true,
   "age": 25
   }

2. **필드 유효성 검증 테스트**
    - 필드 누락 확인 완료

3. **필드 값 유효성 검증 테스트**
    - userid값 MAX 이상일 시 에러 [아이디는 최소 3자리 이상이어야 합니다. ] [완료]
    - password MAX 이상일 시 에러 [비밀번호는 최소 8자리 이상이어야 합니다. ] [완료]
    - username 중복가능 ( 닉네임으로 하면 중복 불가로 변경해야할 듯)
    - email 중복불가
    - email MAX 이상일 시 에러 [회원가입 중 오류가 발생했습니다.] [완료]
    - 나이 -1값 들어감(범위 지정) [완료]

5. **비밀번호 보안 테스트**
    - 비밀번호가 적절히 해시되어 저장되는지 확인.

6. **알림 구독 여부 테스트**
    - `alarm` 필드의 기본값 null설정 [true 기본값] [완료]

7. **취미 필드 관련 테스트 (`hobbies`)**
    - 취미 필드가 비어 있거나 여러 개일 때의 처리 확인.
    - BASE 없어도 될듯

8. **역할(Role) 설정 테스트**
    - 기본 사용자 역할이 `ROLE_USER`로 설정되는지 확인.

12. **성공적인 응답 구조 확인**
    - 회원가입 성공 시 반환되는 응답의 상태 코드와 구조 확인.

13. **예외 처리 테스트**
    - 데이터베이스 연결 문제 등 내부 오류 발생 시 예외 처리가 제대로 이루어지는지 확인.

## 로그인 기능 테스트 계획

1. **성공적인 로그인 테스트**
   {
   "userId": "testuser",
   "password": "password1234"
   }
2. 아이디 또는 비밀번호 누락 테스트
    - 아이디 누락 시 401반환
    - 비밀번호 누락 시 401반환
    - 아이디 비밀번호 필수 입력 에러 발생 안함
3. 잘못된 아이디, 비밀번호 테스트
    - 아이디 또는 비밀번호가 잘못됬다는 에러 발생 안함
    - 401호출됨
    - 잘못된 비밀번호 요청 401 [잘못된 사용자명 또는 비밀번호입니다.] (추가)
    - 존재하지 않는 사용자 404 [사용자가 존재하지 않습니다.] (추가)
    - Brute Force 방지 429 [같은 IP에서 비밀번호 틀린 시도가 반복되었을 경우] (추가)

4. 토큰관리
    - JWT필터 부분
    - // Token 만료 여부 확인, 만료시 다음 필터로 넘기지 않음!!
      try {
      jwtUtil.isExpired(accessToken);
      } catch (ExpiredJwtException e) {
      log.error("JWTFilter - Access token expired for token: {}", accessToken);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().print("Access token expired. Please login again.");
      return;
      }
    - access토큰 만료 시 refresh토큰이 있다면 새로운 access토큰 재발급 로직 추가
    - access토큰 만료 시 refersh토큰 사용 테스트 완료
    - refresh토큰 만료 시 스케줄러 작동 DB삭제 테스트 완료

5. 사용자 기능
   - getHobbyById public 전환?
   - 나의 회원정보 조회 hobby 값안들어옴 [완료]
   - 로그아웃 refresh 삭제되는지 테스트 완료 자동으로 쿠키에 refresh토큰 저장됨
   - 회원정보 변경 기능 
     - 회원정보 누락 시 기본값 적용
     - 알람 변경 불가
     - username 중복 가능
   - 비밀번호 변경
     - PasswordUpdateRequest 최대 길이 설정 필요
   - 회원탈퇴
     - 논리적 삭제 가능하도록 deleted 값 변경
     - 논리적 삭제 시 deleted_at 시간 설정
     - 유효성 검사 deleted 0값만 조회하여 실행하도록 로직 추가

6. 모임 기능
   - 모임 리스트 조회 테스트 완료
   - 모임 생성 매핑값 추가
   - 모임 생성 기능 테스트 완료
     - [MAX시 서버 오류]
     - 모임 이름 유효성 검사 완료
     - 모임 설명 유효성 검사 완료 
     - 모임 위치 유효성 검사 완료
   - 모임 수정 기능 테스트 완료
     - [max값 서버 오류]
     - 모임 이름 유효성 검사 완료
     - 모임 설명 유효성 검사 완료
     - 모임 참가자 인원 검사 완료
     - 모임 삭제 검사 완료 [논리적 삭제 로직 추가]