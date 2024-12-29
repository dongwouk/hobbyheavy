<br/><br/>
![header](https://capsule-render.vercel.app/api?type=Venom&color=auto&height=150&section=header&text=HobbyHeavy&fontSize=40)
<br/><br/>
다양한 취미를 가진 사람들이 모여 서로의 취미를 공유하고, 같은 관심사를 가진 사람들과 소통할 수 있는 플랫폼입니다.<br/><br/><br/>

## 🔍 프로젝트 개요  
- **기간:** 2024.10.23 ~ 2024.11.29  
- **팀원:** 이은선, 권동욱, 유수호  
- **워크 스페이스:** [Notion 링크](https://www.notion.so/127a0ad20d79801f9ad3dcf55e11bb7b)  



## 🛠️ 기술 스택  
- **Java (JDK 17)**  
- **Apache 3.x, Apache Tomcat 10.0**  
- **Spring Boot 3.3.5, Spring Security, JPA**  
- **MySQL 8.x**  
- **IntelliJ IDEA, MySQL WorkBench 8.0, Postman**  
- **JWT, Validation, Swagger, JUnit, SendGrid, Lombok**  



## 🎯 역할 및 기여  
- **RESTful API 설계 및 구현:** 사용자 인증, 스케줄 관리, 모임 생성 등 주요 기능 API 구현.  
- **사용자 인증 및 권한 관리:** Spring Security와 JWT를 활용하여 안전한 인증 시스템 설계.  
- **스케줄 생성 및 알림 기능 개발:** 실시간 알림과 비동기 작업 도입으로 성능 최적화.  
- **데이터베이스 설계:** 효율적인 데이터 저장 및 조회를 위한 ERD 설계 및 쿼리 최적화.  
- **팀 협업:** 코드 리뷰와 의견 교환을 통해 코드 품질을 향상시키고 협업 능력을 강화.  



## 📑 주요 기능  

### 👥 사용자 인증 및 회원 서비스  
- 회원가입/로그인 (아이디, 이메일 중복 검사 및 비밀번호 암호화).  
- JWT 기반 인증 및 Refresh Token 발급/갱신.  
- 회원정보 수정 및 조회, 회원 탈퇴.  

### 👫 모임 서비스  
- 모임 생성, 수정, 삭제 및 검색.  
- 모임 상세 조회 및 참여 신청/승인/거절 관리.  
- 나의 모임 목록 조회.  

### 📝 댓글 서비스  
- 댓글 생성, 조회, 수정, 삭제.  

### ⏰ 스케줄 서비스  
- 스케줄 생성, 조회, 수정, 삭제.  
- 투표 기반 스케줄 확정 및 알림 전송.  
- 참여한 모임의 스케줄 목록 조회.  

### 🌟 후기 서비스  
- 후기 작성, 수정, 삭제 및 별점 평가.  



## 📱 와이어 프레임  
![Frame 1](https://github.com/user-attachments/assets/daf19b09-5047-4995-98d8-b6da094ba42b)



## ⚙️ 시스템 아키텍처  
**Layered Architecture** (계층형 아키텍처) 기반 설계.
![hobbyheavy아키텍처](https://github.com/user-attachments/assets/19f9affb-208d-4322-a5e8-394903a41b59)

  - **Auth:** 인증 및 권한 관리.  
  - **Config:** CORS 설정, JWT 인증, Swagger UI 통합.  
  - **Controller Layer:** 사용자 요청 처리 및 서비스 호출.  
  - **Service Layer:** 비즈니스 로직 처리 및 데이터 유효성 검증.  
  - **Repository Layer:** 데이터베이스 저장/조회 및 논리적 삭제 처리.  
  - **DTO:** 데이터 전송 객체를 통한 계층 간 데이터 전달.  
  - **Entity:** 테이블 매핑 객체 설계.  
  - **Exception:** 커스텀 예외 처리 및 표준화된 응답 제공.  
  - **Util:** 재사용 가능한 유틸리티 클래스 모음.  



## 🗺️ ERD  
![hobbyheavy (2)](https://github.com/user-attachments/assets/9a043c7f-4c70-40f9-873c-a04f7f883f24)  
[ERD 상세 보기](https://www.erdcloud.com/d/Yjk8NgXHZkgntsodG)  

**성과:**  
- **스케줄 생성 및 알림 기능** 구현 중 발생한 성능 문제를 해결하기 위해 비동기 작업을 도입하여 성능을 개선했습니다.  
- 팀원들과의 **코드 리뷰**를 통해 협업의 중요성을 배우고, 이를 바탕으로 코드 품질을 개선했습니다.  
- 이번 프로젝트를 통해 **성능 최적화** 및 **효율적인 팀 협업**에 대한 깊은 이해와 경험을 쌓았습니다.

