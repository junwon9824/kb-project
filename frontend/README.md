# KB Bank Frontend

KB Bank 4조 프로젝트의 React 프론트엔드 애플리케이션입니다.

## 주요 기능

- **로그인/회원가입**: 사용자 인증 및 회원 관리
- **메인 페이지**: 주요 기능으로의 네비게이션
- **송금**: 계좌 간 송금 기능
- **계좌조회**: 사용자 계좌 목록 및 잔액 조회
- **즐겨찾기**: 자주 사용하는 계좌 관리
- **음성 인식**: WebSocket 기반 음성 인식 및 합성
- **뉴스**: 뉴스 목록 및 상세보기 (음성 읽기 기능 포함)

## 기술 스택

- **React 18.2.0**: 최신 React 버전
- **React Router 6.11.2**: 클라이언트 사이드 라우팅
- **Axios 1.4.0**: HTTP 클라이언트
- **WebSocket**: 실시간 음성 인식 통신
- **Speech API**: 음성 인식 및 합성
- **Bootstrap 5.3.0**: UI 프레임워크

## 설치 및 실행

### 1. 의존성 설치

```bash
npm install
```

### 2. 개발 서버 실행

```bash
npm start
```

애플리케이션이 `http://localhost:3000`에서 실행됩니다.

### 3. 프로덕션 빌드

```bash
npm run build
```

## 프로젝트 구조

```
src/
├── components/          # 재사용 가능한 컴포넌트
│   ├── Header.jsx      # 공통 헤더
│   ├── TransferModal.jsx # 송금 모달
│   ├── NewsModal.jsx   # 뉴스 상세 모달
│   └── *.css           # 컴포넌트별 스타일
├── pages/              # 페이지 컴포넌트
│   ├── LoginPage.jsx   # 로그인/회원가입
│   ├── MainPage.jsx    # 메인 페이지
│   ├── TransferPage.jsx # 송금 페이지
│   ├── BankAccountList.jsx # 계좌조회
│   ├── BookMarkList.jsx # 즐겨찾기
│   ├── VoiceRecognition.jsx # 음성 인식
│   ├── NewsList.jsx    # 뉴스 목록
│   ├── LogList.jsx     # 계좌내역
│   ├── LogList2.jsx    # 장애인용 계좌내역
│   └── *.css           # 페이지별 스타일
├── App.jsx              # 메인 앱 컴포넌트
├── App.css             # 전역 스타일
├── index.js            # 앱 진입점
└── index.css           # 기본 스타일
```

## API 연동

프로젝트는 백엔드 API와 연동되도록 설계되었습니다:

- **Proxy 설정**: `http://localhost:8080`으로 API 요청 전달
- **RESTful API**: 표준 HTTP 메서드 사용
- **WebSocket**: 음성 인식을 위한 실시간 통신

## 주요 특징

### 1. 반응형 디자인

- 모바일 친화적 레이아웃
- Bootstrap 기반 반응형 컴포넌트

### 2. 접근성

- 장애인을 위한 별도 인터페이스 제공
- 음성 인식 및 합성 기능

### 3. 사용자 경험

- 직관적인 네비게이션
- 실시간 피드백
- 모달 기반 상세보기

### 4. 성능 최적화

- React Hooks 활용
- 컴포넌트 최적화
- 효율적인 상태 관리

## 개발 가이드

### 컴포넌트 작성 규칙

- 함수형 컴포넌트 사용
- Hooks 기반 상태 관리
- CSS 모듈 또는 인라인 스타일 사용

### API 호출 패턴

```javascript
import axios from "axios";

// GET 요청
const response = await axios.get("/api/endpoint");

// POST 요청
const response = await axios.post("/api/endpoint", data);

// 에러 처리
try {
  const response = await axios.get("/api/endpoint");
} catch (error) {
  console.error("API 호출 실패:", error);
}
```

### 라우팅 설정

```javascript
import { BrowserRouter, Routes, Route } from "react-router-dom";

<Routes>
  <Route path="/users/login" element={<LoginPage />} />
  <Route path="/users/main" element={<MainPage />} />
  <Route path="/transfer" element={<TransferPage />} />
</Routes>;
```

## 배포

### 1. 빌드

```bash
npm run build
```

### 2. 정적 파일 서빙

빌드된 `build` 폴더의 내용을 웹 서버에 배포하세요.

## 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 팀 정보

- **프로젝트명**: KB Bank 4조
- **기술 스택**: React, Spring Boot, MySQL
- **목적**: 은행 서비스 시뮬레이션
