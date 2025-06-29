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
- **데이터베이스**: MySQL 8.0 + Redis (캐싱)
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
- `Redis` (캐싱)

### External APIs
- `ChatGPT API`
- `WebSocket` (실시간 통신)

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

**원인:**
- MySQL 컨테이너가 실행되지 않음
- 데이터베이스 연결 설정 문제

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
- 로그 내역은 Redis에서 캐싱됩니다
- 캐시 무효화: 송금/입금 시 자동으로 해당 계좌의 캐시가 삭제됩니다
- Redis CLI 접속:
```bash
docker exec -it my-redis redis-cli
keys *
get junwon9824-9824  # 특정 키 조회
del junwon9824-9824  # 캐시 삭제
```

## 프로젝트 구조
- **백엔드**: Spring Boot + JPA + MySQL + Redis
- **프론트엔드**: React + Vite
- **데이터베이스**: MySQL (메인), Redis (캐시)
- **캐싱**: Redis를 사용한 로그 내역 캐싱

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

 
  
 
  
