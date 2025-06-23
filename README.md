# 🚗 랜덤여행생성기 Backend

**"여행지 추천부터 최적 경로 생성까지. GPT와 Kakao API를 연동한 Spring Boot 기반 여행 추천 백엔드 시스템입니다."**


---


## ⚙️ 기술 스택
- **Java 17**
- **Spring Boot 3**
- **Spring Security + OAuth2 (Kakao 로그인)**
- **JWT (토큰 인증 시스템)**
- **JPA (Hibernate)** – ORM 기반 DB 연동
- **MySQL** – 사용자, 여행, 장소, 경로 데이터 저장
- **RESTful API 설계**
- **OpenAI API** – GPT 기반 장소 추천
- **Kakao Mobility API** – 경로 최적화
- **Kakao Maps Api** - 지도에 경로 polyline, 마커 표시
- **JCloud** – 배포 서버
- **GitHub Actions** – CI/CD 자동화


---


## 📂 주요 폴더 구조

src/
src/main/java/com/randomtrip/backend/
├── config/         # 보안, CORS, JWT, OAuth2 등 설정 클래스
├── controller/     # REST API 컨트롤러 (요청 핸들링)
├── dto/            # 요청/응답 데이터 전송 객체
├── entity/         # JPA 엔티티 클래스 (DB 테이블 매핑)
├── external/       # Kakao Mobility API 외부 API 연동 모듈
├── repository/     # JPA Repository (DB 접근 인터페이스)
├── service/        # 핵심 비즈니스 로직 처리
├── util/           # 공통 유틸 함수 및 상수
└── RandomTripBackendApplication.java  # Spring Boot 진입점


---


## 🧭 주요 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| `POST` | `/trip/random` | 지역 기반 랜덤 장소 추천 |
| `POST` | `/trip/plan`   | GPT 기반 여행지 추천 (분위기 + 지역) |
| `POST` | `/trip/confirm`| 출발지 + 여행지 확정 후 DB 저장 |
| `GET`  | `/trip/my`     | 사용자의 최근 여행 경로 조회 |


---


## 🔐 인증 및 보안

- **Kakao OAuth2 로그인**  
  - `/oauth/kakao`로 로그인 → Kakao에서 인증 후 리다이렉트  
- **JWT 발급 및 검증**  
  - 로그인 성공 시 JWT 발급  

---


## 📦 실행 방법

```bash
# 1. 프로젝트 클론
git clone https://github.com/imsh429/random-trip-backend.git
cd random-trip-backend

## 2. 환경 변수 설정

src/main/resources/application.yml 파일을 생성하고 아래 항목을 입력하세요:

- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- jwt.secret
- kakao.client-id
- kakao.redirect-uri
- openai.api.key

➡ 실제 값은 각자 발급받은 키로 직접 설정해야 합니다.

# 3. 개발 서버 실행
./gradlew bootRun

# 또는 JAR 파일로 실행하려면:
./gradlew build
java -jar build/libs/*.jar
