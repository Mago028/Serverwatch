# 🚀 Serverwatch

> 📡 Spring Boot 기반 서버 모니터링 시스템  
> 서버 메트릭을 주기적으로 수집·저장하고 웹에서 조회할 수 있도록 구현한 프로젝트



## 📌 프로젝트 개요

Serverwatch는 서버 상태(CPU, Memory, Disk, Load Average 등)를  
주기적으로 수집하고 데이터베이스에 저장하는 백엔드 중심 프로젝트입니다.

단순 CRUD 구현을 넘어 다음과 같은 구조 설계를 목표로 진행하였습니다.

- 🔐 JWT 기반 인증/인가 구조 설계
- 🐳 Docker Compose 기반 실행 환경 구성
- ⚙ 환경 변수(.env) 분리
- 🌍 실제 배포 가능한 구조 설계



## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| 🧠 Backend | Spring Boot, Spring Security (JWT), JPA (Hibernate) |
| 🗄 Database | MySQL |
| 🐳 Infra | Docker, Docker Compose |
| 💻 Frontend | React (Vite + TypeScript) |



## ⚙ 주요 기능

### 🔐 인증 / 인가

- 회원가입
- 로그인 (JWT 발급)
- 인증이 필요한 API 보호 처리
- Stateless 인증 구조



### 🖥 서버 관리

- 서버 등록
- 서버 목록 조회
- 서버 상태 조회



### 📊 메트릭 수집

- CPU 사용률
- Memory 사용률
- Disk 사용률
- Load Average
- 30초 주기 스케줄링 기반 자동 수집



## 🏗 시스템 아키텍처

```bash
[ React Frontend ]
        ↓
[ Spring Boot API Server ]
        ↓
[ MySQL Database ]
        ↑
[ Metric Scheduler (30초 주기 실행) ]
```



## 🚀 실행 방법

### 📁 환경 변수 파일 생성

```bash
cp .env.example .env
```

`.env` 파일에 DB 정보 및 JWT_SECRET을 입력합니다.



### 🐳 Docker Compose 실행

```bash
docker compose up -d --build
```



### ✅ 실행 확인

```bash
docker ps
```



## 🔎 API 테스트 예시

> ⚠ 아래 계정 정보는 예시용 더미 데이터입니다.  
> 실제 사용 시에는 로컬 환경에서 회원가입 후 테스트하시기 바랍니다.

### 📝 회원가입

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@example.com","password":"P@ssw0rd!123"}'
```



### 🔑 로그인 (JWT 발급)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"P@ssw0rd!123"}'
```



### 🔓 인증 API 호출

```bash
curl http://localhost:8080/api/servers \
  -H "Authorization: Bearer <JWT_TOKEN>"
```



## 🧯 트러블슈팅

### ⚠ MySQL 포트 충돌

- 3306 포트 사용 중 문제 발생
- docker-compose에서 3307 포트로 변경하여 해결

### 🚫 JWT 403 오류

- Authorization 헤더 형식 오류
- `Bearer <TOKEN>` 형식으로 전달하여 해결

### 🔒 GitHub Push Protection 차단

- Slack Webhook 제거
- 환경 변수 분리 및 커밋 히스토리 정리



## 📈 향후 개선 방향

- 🔔 서버 상태 임계값 기반 알림 기능 확장
- ⚡ Redis 캐시 적용
- 🔄 GitHub Actions 기반 CI/CD 구성
- 🌐 Nginx + HTTPS 적용



## 👨‍💻 프로젝트 회고

Serverwatch는 실제 배포 가능한 구조를 설계하는 것을 목표로 진행한 프로젝트입니다.

JWT 인증 처리, Docker 기반 실행 환경 구성, 환경 변수 분리 등을 직접 구현하며  
서비스 구조 전반에 대한 이해도를 높일 수 있었습니다.
