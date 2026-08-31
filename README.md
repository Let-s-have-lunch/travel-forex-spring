# ✈️ Travel Forex Service (Backend)

해외여행자를 위한 실시간 환율 계산, 외화 지갑 관리, 그리고 여행 경비 및 지출 내역을 추적하는 서비스의 백엔드(Spring Boot) API입니다.

---

## 🛠 Tech Stack

*   **Framework:** Spring Boot (Web, Data JPA, Security, Validation)
*   **Language:** Java
*   **Security:** Spring Security, JWT (JSON Web Token)
*   **Database:** RDBMS (JPA / Hibernate)
*   **External APIs:**
    *   Frankfurter API (과거 환율 데이터 수집)
    *   ExchangeRate-API (실시간 최신 환율)
*   **Architecture:** Layered Architecture (Controller - Service - Repository)

---

## ✨ Key Features

1.  **환율 데이터 관리 (Exchange Rates)**
    *   **실시간 환율:** `ExchangeRate-API`를 연동하여 실시간 환율을 조회하고 인메모리에 캐싱하여 응답 속도 최적화.
    *   **시계열 이력 백필(Backfill):** 애플리케이션 가동 시 `ExchangeRateHistorySeeder`가 빈 구간의 과거 환율 데이터를 `Frankfurter API`를 통해 1년 치 자동 백필.
    *   **스케줄링:** 10분 단위로 현재 환율을 DB에 스냅샷으로 기록하여 차트용 데이터 제공.

2.  **외화 지갑 및 거래 내역 (Wallet & Transactions)**
    *   다국어 통화(CurrencyCode)별 가상 지갑 생성 및 잔액 관리.
    *   입출금 및 환전 내역 추적.
    *   **커서 기반 페이지네이션(Cursor Pagination)**을 적용하여 무한 스크롤 형태의 거래 내역 조회를 최적화.

3.  **여행 및 지출 관리 (Trips & Expenses)**
    *   여행 일정(시작일/종료일) 및 총 예산 설정.
    *   해외 현지 결제 내역(TripExpense) 등록 시 **지갑 연동 결제**를 지원하여 자동으로 지갑 잔액 차감 및 출금 트랜잭션(WalletTransaction) 동시 생성.
    *   자동 원화(KRW) 환산 금액 저장.

4.  **회원 및 보안 (Users & Security)**
    *   회원가입 및 JWT 기반 Stateless 인증/인가 (`JwtAuthenticationFilter`).
    *   BCrypt를 이용한 비밀번호 암호화.
    *   Global `Soft Delete` (`@SQLDelete`, `@SQLRestriction`) 적용으로 데이터 복구 및 무결성 유지.

5.  **고객지원 (Notices & Inquiries)**
    *   공지사항 및 1:1 문의 게시판 기능.
    *   사용자와 관리자(Admin) 간의 답변 상태(PENDING/ANSWERED) 관리.

---

## 📂 Project Structure

```text
├── client/          # 외부 API 통신 클라이언트 (Frankfurter 등)
├── config/          # Spring Security, JWT, CORS, RestTemplate 설정
├── controller/      # REST API 엔드포인트
├── domain/          # JPA 엔티티 및 Enum (BaseTimeEntity 포함)
├── dto/             # Request / Response DTO 및 Pagination 공통 객체
├── exception/       # 전역 예외 처리 (@RestControllerAdvice)
├── initializer/     # 서버 구동 시 초기 데이터 세팅 (Rate Seeder)
├── repository/      # Spring Data JPA Repository
├── schedular/       # 백그라운드 스케줄러 (환율 스냅샷)
├── service/         # 핵심 비즈니스 로직 및 트랜잭션 관리
└── utils/           # JWT 헬퍼 및 유틸리티 클래스
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Java 17+ (or 21)
- Gradle (또는 IDE 내장 Gradle Wrapper)
- RDBMS (MariaDB / MySQL 등)

### 2. Environment Variables
프로젝트 루트 또는 실행 환경에 다음 환경변수(또는 `application-dev.yml`)를 설정해야 합니다.

```yaml
# JWT 시크릿 키 (Base64 인코딩 권장)
jwt:
  secret: your_jwt_secret_key_string_here_must_be_long_enough

# 실시간 환율 API 키 (ExchangeRate-API)
exchange-rate:
  api-key: your_exchangerate_api_key_here
```

### 3. Run Development Server
터미널에서 아래 명령어를 실행하여 서버를 구동합니다.

```bash
# Windows
gradlew bootRun

# macOS / Linux
./gradlew bootRun
```