# 🖥️ WEB - 관리자 웹 시스템

이 디렉토리는 GuardPlatform 프로젝트의 **Spring Boot 기반 서버 및 관리자 웹 시스템**입니다.

OCR 모듈에서 전달된 차량번호와 BLE 기반 위치 데이터를 서버에서 연동해  
**등록 차량 관리, 전체 차량 출입 기록, 미등록 차량 경고 및 최근 감지 위치 관리** 기능을 제공합니다.

또한 SSE(Server-Sent Events)를 활용해 **미등록 차량 감지 및 위치 변경 이벤트를 관리자에게 실시간으로 전달**합니다.

---

## 📌 주요 기능

- ✅ 관리자 로그인 및 JWT 기반 인증
- 🚗 사전 등록 차량 관리
- 📋 등록·미등록 차량 전체 출입 기록 저장 및 조회
- 🚨 미등록 차량 경고 생성 및 조회
- 📍 BLE 식별자 기반 미등록 차량 최근 감지 위치 갱신
- 🔎 미등록 차량 기록 필터링 (날짜, 차량번호, 위치)
- 🔔 SSE 기반 미등록 차량 감지 및 위치 변경 실시간 알림
- 🖥️ 등록 차량·출입 기록·미등록 차량 조회를 위한 관리자 웹페이지
- 📄 REST API 제공

---

## 🔄 서비스 흐름

**번호판 이미지 OCR  
→ 차량번호 서버 전달  
→ Spring Boot에서 등록 여부 판별  
→ 전체 차량 출입 기록 저장  
→ 미등록 차량 경고 생성 및 SSE 알림  
→ BLE RSSI 기반 최근 감지 위치 판별  
→ BLE 식별자 기준 미등록 차량 위치 갱신  
→ 위치 변경 SSE 알림**

---

## 🛠️ 기술 스택

### Frontend
- HTML
- CSS
- JavaScript

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security
- JWT
- Spring Data JPA
- SSE (`SseEmitter`)

### Database
- MySQL

### Integration
- OCR 차량번호 데이터 연동
- BLE 식별자 및 위치 데이터 연동

### Build
- Gradle

---

## 📁 디렉토리 구조

```text
WEB/
├── build.gradle
├── settings.gradle
├── gradle/
├── gradlew
├── gradlew.bat
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nagne/carguardplatform/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── util/
│   │   └── resources/
│   │       ├── static/
│   │       └── application.yml
│   └── test/
```

---

## 🚀 실행 방법

### 1. MySQL 데이터베이스 구성

로컬 MySQL에 프로젝트에서 사용할 데이터베이스를 생성합니다.

```sql
CREATE DATABASE guard_db;
```

### 2. 환경변수 설정

DB 비밀번호와 JWT Secret은 소스 코드에 직접 저장하지 않고 환경변수로 관리합니다.

```env
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
```

`application.yml`에서는 다음과 같이 환경변수를 참조합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/guard_db?serverTimezone=Asia/Seoul
    username: root
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000

server:
  port: 8080
```

실제 값이 들어 있는 `.env` 파일은 Git에 포함하지 않습니다.

```gitignore
.env
```

### 3. 실행

```bash
cd WEB
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

---

## 📮 주요 API

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/vehicle/entry` | 차량번호와 BLE 식별자를 전달해 차량 입차 처리 |
| GET | `/api/vehicle/history` | 전체 차량 출입 기록 조회 |
| POST | `/api/warning/location` | BLE 식별자 기준 미등록 차량 최근 위치 갱신 |
| GET | `/api/warning/stream` | SSE 실시간 이벤트 스트림 연결 |

### 차량 입차 요청 예시

```json
{
  "plateNumber": "12가3456",
  "bleId": "BLE_DEVICE_ID"
}
```

차량 입차 시 서버에서 등록 여부를 확인하고, 등록 여부와 관계없이 출입 기록을 저장합니다.  
미등록 차량인 경우 별도의 경고 데이터를 생성하고 SSE를 통해 관리자에게 알림을 전달합니다.

### 위치 갱신 요청 예시

```json
{
  "bleId": "BLE_DEVICE_ID",
  "location": "A구역"
}
```

BLE 식별자를 기준으로 해당 미등록 차량의 최근 감지 위치를 갱신합니다.

---

## 🔔 SSE 실시간 알림

관리자 웹에서는 `EventSource`를 이용해 `/api/warning/stream`을 구독하고 서버에서 발생하는 이벤트를 실시간으로 수신합니다.

### `warning` 이벤트

미등록 차량이 감지되었을 때 전송됩니다.

```text
차량번호: 12가3456
```

### `location` 이벤트

미등록 차량의 최근 감지 위치가 확인되거나 변경되었을 때 전송됩니다.

```text
12가3456 · A구역
```

이를 통해 별도의 재조회 없이 **미등록 차량 감지와 위치 변경 정보를 브라우저 알림으로 확인**할 수 있습니다.

---

## 🔐 보안 설정

DB 비밀번호와 JWT Secret과 같은 민감 정보는 환경변수로 관리합니다.

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

실제 값이 포함된 `.env` 파일은 `.gitignore`에 등록해 GitHub에 업로드되지 않도록 설정합니다.