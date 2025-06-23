// API 기본 설정
const API_BASE_URL = '';

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
    LOGIN: '/api/auth/login',
    SIGNUP: '/api/user/signup',
    LOGOUT: '/api/user/logout',
    PROFILE: '/api/user/profile',
  },
  
  // 은행 계좌 관련
  BANK_ACCOUNT: {
    LIST: '/api/bankaccount/list',
    TRANSFER: '/api/bankaccount/transfer',
    DETAIL: '/api/bankaccount/detail',
  },
  
  // 로그 관련
  LOG: {
    LIST: '/api/log/list',
    CREATE: '/api/log/create',
    DELETE: '/api/log/delete',
  },
  
  // 북마크 관련
  BOOKMARK: {
    LIST: '/api/bookmark/list',
    CREATE: '/api/bookmark/create',
    DELETE: '/api/bookmark/delete',
    UPDATE: '/api/bookmark/update',
  },
  
  // 음성 인식 관련
  VOICE: {
    RECOGNIZE: '/api/voice/recognize',
    WEBSOCKET: '/api/voice/websocket',
  },
  
  // 뉴스 관련
  NEWS: {
    LIST: '/api/news/list',
    DETAIL: '/api/news/detail',
    SEARCH: '/api/news/search',
  }
}; 
