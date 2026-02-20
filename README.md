📡 Serverwatch

Spring Boot 기반 서버 모니터링 시스템입니다.
서버의 CPU, Memory, Disk, Load Average 등의 메트릭을 주기적으로 수집하고 저장하여 웹에서 조회할 수 있도록 구현하였습니다.

📌 프로젝트 개요

Serverwatch는 서버 상태를 자동으로 수집하고 기록하는 백엔드 중심 프로젝트입니다.
단순 기능 구현을 넘어 Docker 기반 실행 환경과 JWT 인증 구조를 포함하여 실제 배포 가능한 형태로 설계하였습니다.

🛠 기술 스택
Backend

Spring Boot

Spring Security (JWT)

JPA (Hibernate)

Database

MySQL

Infra

Docker

Docker Compose

Frontend

React (Vite + TypeScript)

⚙ 주요 기능
🔐 인증 / 인가

회원가입 및 로그인

JWT 토큰 발급

인증이 필요한 API 보호 처리

🖥 서버 관리

서버 등록

서버 목록 조회

서버 상태 조회

📊 메트릭 수집

CPU 사용률

Memory 사용률

Disk 사용률

Load Average

30초 주기 스케줄링 기반 자동 수집

🧹 유지 관리

공통 예외 처리 (@ControllerAdvice)

환경 변수(.env) 기반 설정 분리

Docker Compose 기반 실행 환경 구성

🏗 시스템 구조
[ React Frontend ]
        ↓
[ Spring Boot API Server ]
        ↓
[ MySQL Database ]
        ↑
[ Metric Scheduler (30초 주기 실행) ]

🚀 실행 방법
1️⃣ 환경 변수 파일 생성
cp .env.example .env


.env 파일에 DB 정보 및 JWT_SECRET을 입력합니다.

2️⃣ 애플리케이션 실행
docker compose up -d --build

3️⃣ 실행 확인
docker ps

🔎 API 테스트 예시
회원가입
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@test.com","password":"admin1234"}'

로그인 (JWT 발급)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin1234"}'

인증 API 호출
curl http://localhost:8080/api/servers \
  -H "Authorization: Bearer <JWT_TOKEN>"

⚠ 트러블슈팅
1️⃣ MySQL 포트 충돌 문제

기존 3306 포트 사용 중

docker-compose에서 3307 포트로 변경하여 해결

2️⃣ JWT 인증 실패 (403)

Authorization 헤더에 Bearer <TOKEN> 형식으로 전달하여 해결

3️⃣ GitHub Push Protection 차단

Slack Webhook 제거

환경변수 분리 후 커밋 히스토리 정리

📈 향후 개선 방향

서버 상태 임계값 기반 알림 기능 확장

Redis 캐시 적용

GitHub Actions 기반 자동 배포 구성

Nginx + HTTPS 적용

👨‍💻 프로젝트 회고

단순 기능 구현을 넘어서 실제 배포 가능한 구조를 설계하는 것을 목표로 진행하였습니다.
JWT 인증 처리, 환경 변수 분리, Docker 기반 실행 환경 구성 등을 직접 구현하며 서비스 구조 전반에 대한 이해를 높일 수 있었습니다.