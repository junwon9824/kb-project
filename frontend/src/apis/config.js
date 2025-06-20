// API 기본 설정
const API_BASE_URL = 'http://localhost:8080/api';

export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  TIMEOUT: 10000,
  HEADERS: {
    'Content-Type': 'application/json',
  }
};

// API 엔드포인트 상수
export const API_ENDPOINTS = {
  // 사용자 관련
  USER: {
    LOGIN: '/user/login',
    SIGNUP: '/user/signup',
    LOGOUT: '/user/logout',
    PROFILE: '/user/profile',
  },
  
  // 은행 계좌 관련
  BANK_ACCOUNT: {
    LIST: '/bankaccount/list',
    TRANSFER: '/bankaccount/transfer',
    DETAIL: '/bankaccount/detail',
  },
  
  // 로그 관련
  LOG: {
    LIST: '/log/list',
    CREATE: '/log/create',
    DELETE: '/log/delete',
  },
  
  // 북마크 관련
  BOOKMARK: {
    LIST: '/bookmark/list',
    CREATE: '/bookmark/create',
    DELETE: '/bookmark/delete',
    UPDATE: '/bookmark/update',
  },
  
  // 음성 인식 관련
  VOICE: {
    RECOGNIZE: '/voice/recognize',
    WEBSOCKET: '/voice/websocket',
  },
  
  // 뉴스 관련
  NEWS: {
    LIST: '/news/list',
    DETAIL: '/news/detail',
    SEARCH: '/news/search',
  }
}; 