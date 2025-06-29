# kb-project

# 디지털 취약계층을 위한 금융 서비스 
시각장애인과 같은 디지털 취약계층을 위해 송금, 계좌조회 등의 금융 서비스를 편하게 음성으로 
사용할수 있게 하는 프로젝트 입니다.

## 프로젝트 구조 변경 이력

### Frontend 변경: Thymeleaf → React
- **기존**: Spring Boot + Thymeleaf 템플릿 엔진
- **변경**: React + Vite 기반 SPA (Single Page Application)
- **변경 이유**: 
  - 더 나은 사용자 경험 (SPA)
  - 컴포넌트 기반 개발로 유지보수성 향상
  - API 기반 아키텍처로 백엔드와 프론트엔드 분리

### 기술 스택 변경
- **Frontend**: React 18, Vite, Axios
- **Backend**: Spring Boot 3.0.5 (REST API)
- **데이터베이스**: MySQL 8.0 + Redis (캐싱 + Fallback 기능)
- **인증**: JWT 토큰 기반

## 흐름도
![image](https://github.com/kb-project-4/kb-project/assets/42957005/983acbe3-8395-4219-8b07-9e6ec84eb940)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/d89a563e-07f3-407c-8959-e48d19ca8383)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/dcf96435-ff6a-4ce9-a917-b4615af451dc)
![image](https://github.com/kb-project-4/kb-project/assets/42957005/e40de793-dba3-4894-9470-8121a413b60c)

## 사용된 기술 스택

### Frontend
- `React 18`
- `Vite` (빌드 도구)
- `Axios` (HTTP 클라이언트)
- `React Router` (라우팅)

### Backend
- `JAVA 17`
- `Spring Boot 3.0.5`
- `Spring Security` (JWT 인증)
- `Spring Data JPA`
- `Spring Data Redis`

### Database & Cache
- `MySQL 8.0.27` (메인 데이터베이스)
- `Redis` (캐싱 + Fallback 기능)
  - 로그 내역 캐싱으로 빠른 조회
  - Redis 실패 시 MySQL에서 직접 조회하는 Fallback 메커니즘

### External APIs
- `ChatGPT API`
- `WebSocket` (실시간 통신)

## 주요 기능

### 1. 사용자 인증
- JWT 토큰 기반 로그인/로그아웃
- 사용자 프로필 관리

### 2. 계좌 관리
- 계좌 생성/삭제
- 계좌 목록 조회
- 주계좌 설정
- 계좌 잔액 조회

### 3. 송금 기능
- 계좌 간 송금
- 송금 내역 조회
- 실시간 거래 로그

### 4. 음성 인식
- WebSocket 기반 실시간 음성 인식
- ChatGPT API 연동 음성 대화

### 5. 뉴스 서비스
- 뉴스 목록 조회
- 뉴스 상세 보기
- 뉴스 검색

### 6. 북마크 기능
- 북마크 생성/삭제
- 북마크 목록 관리

## API 엔드포인트

### 인증 (Authentication)
- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃

### 사용자 (User)
- `GET /api/users/me` - 사용자 프로필 조회
- `POST /api/users` - 회원가입

### 계좌 (Bank Account)
- `GET /api/bankaccounts/user/{userid}` - 사용자 계좌 목록
- `POST /api/bankaccounts` - 계좌 생성
- `DELETE /api/bankaccounts/{id}` - 계좌 삭제
- `PUT /api/bankaccounts/mainAccount/{id}` - 주계좌 설정
- `POST /api/bankaccounts/transfer` - 송금

### 로그 (Log)
- `GET /api/log/{accountNumber}` - 계좌별 거래 내역
- `GET /api/log/nocache/{accountNumber}` - 캐시 미사용 거래 내역

### 북마크 (Bookmark)
- `GET /api/bookmarks` - 북마크 목록
- `POST /api/bookmarks` - 북마크 생성
- `DELETE /api/bookmarks/{id}` - 북마크 삭제

### 음성 인식 (Voice)
- `POST /api/voice` - 음성 인식
- `WebSocket /api/voice/websocket` - 실시간 음성 통신

### 뉴스 (News)
- `GET /api/news` - 뉴스 목록
- `GET /api/news/detail` - 뉴스 상세
- `GET /api/news/search` - 뉴스 검색

## 프로젝트 구조

```
kbproject/
├── frontend/                 # React 프론트엔드
│   ├── src/
│   │   ├── apis/            # API 통신 모듈
│   │   ├── components/      # 재사용 컴포넌트
│   │   ├── pages/          # 페이지 컴포넌트
│   │   └── App.jsx         # 메인 앱 컴포넌트
│   └── package.json
├── kb-project/              # Spring Boot 백엔드
│   ├── src/main/java/
│   │   ├── controller/     # REST API 컨트롤러
│   │   ├── service/        # 비즈니스 로직
│   │   ├── repository/     # 데이터 접근 계층
│   │   ├── entity/         # JPA 엔티티
│   │   ├── dto/           # 데이터 전송 객체
│   │   ├── security/      # 보안 설정
│   │   └── config/        # 설정 클래스
│   └── pom.xml
└── README.md
```

## 설치 및 실행

### 1. Docker 컨테이너 실행
```bash
cd kb-project/src/main/java/com/example/demo/global/redis/
docker-compose up -d
```

### 2. 백엔드 실행
```bash
cd kb-project
./mvnw spring-boot:run
```

### 3. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

## 문제 해결

### MySQL 연결 오류
**오류 메시지:**
```
Communications link failure
Connection refused: no further information
```

**해결 방법:**
1. Docker 컨테이너 상태 확인
```bash
docker ps
```

2. MySQL 컨테이너 재시작
```bash
docker restart my-mysql
```

3. 데이터베이스 연결 테스트
```bash
docker exec -it my-mysql bash
mysql -u root -p
# 비밀번호: ssafy
use kb_project;
show tables;
```

### Redis 캐시 관련
- **캐싱 동작**: 로그 내역은 Redis에 캐시되어 빠른 조회 가능
- **캐시 확인**: `docker exec -it my-redis redis-cli` → `keys *`
- **캐시 삭제**: `del 키이름`

### Redis Fallback (캐시 실패 시 대응)
- **Fallback 동작**: Redis 캐시 접근 실패 시 MySQL에서 직접 조회
- **캐시 무효화**: 송금/입금 시 자동으로 해당 계좌의 캐시가 삭제됨
- **수동 캐시 삭제**: 
  ```bash
  docker exec -it my-redis redis-cli
  keys *                    # 모든 키 확인
  del junwon9824-9824      # 특정 계좌 캐시 삭제
  flushall                 # 모든 캐시 삭제
  ```
- **캐시 상태 확인**: 
  ```bash
  docker exec -it my-redis redis-cli
  info memory              # 메모리 사용량 확인
  info keyspace            # 키스페이스 정보 확인
  ```

## 개발 환경
- **IDE**: IntelliJ IDEA, VS Code
- **데이터베이스**: MySQL 8.0 (Docker)
- **캐시**: Redis (Docker)
- **개발 서버**: 
  - Frontend: http://localhost:3000
  - Backend: http://localhost:8000

# 요구사항 명세서
![image](https://github.com/kb-project-4/kb-project/assets/42957005/834685d5-77b3-456d-9c0b-c49c91500427)
![image](https://github.com/kb-project-4/kb-project/assets/42957005/4ce858dc-ea6b-46ad-a362-8a8eaa63b3d0)
![image](https://github.com/kb-project-4/kb-project/assets/42957005/9d952fa9-1c3d-4f57-a2ae-2e258e0cb3b2)

# 실행화면 
 
![image](https://github.com/kb-project-4/kb-project/assets/42957005/52599226-3c4f-4c02-aa85-2f858ae8e46c)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/98e61037-2a3f-4639-9525-caff212252ea)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/93b114cc-df65-4c59-8b46-199b12f1134c)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/1935c610-43eb-45ed-9483-cc5d039bcec2)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/2646b044-de7c-4138-a418-a8d1f5e9a46d)

![image](https://github.com/kb-project-4/kb-project/assets/42957005/8f220934-51ef-4b49-b370-25bb10ca63a3)

# erd

![image](https://github.com/kb-project-4/kb-project/assets/42957005/829d84a0-351a-428e-a12c-2d5881b97fec)

  
  
  
