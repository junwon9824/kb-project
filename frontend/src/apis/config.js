// API 기본 설정
const API_BASE_URL = "http://localhost:8000";

export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  TIMEOUT: 10000,
  HEADERS: {
    "Content-Type": "application/json",
  },
};

// API 엔드포인트 상수
export const API_ENDPOINTS = {
  // 사용자 관련
  USER: {
    LOGIN: "/api/auth/login",
    SIGNUP: "/api/users",
    LOGOUT: "/api/users/logout",
    PROFILE: "/api/users/me",
  },

  // 은행 계좌 관련
  BANK_ACCOUNT: {
    LIST: "/api/bankaccounts",
    CREATE: "/api/bankaccounts",
    DELETE: "/api/bankaccounts",
    SET_MAIN: "/api/bankaccounts/mainAccount",
    TRANSFER: "/api/bankaccounts/transfer",
    DETAIL: "/api/bankaccounts",
  },

  // 로그 관련
  LOG: {
    LIST: "/api/log",
    CREATE: "/api/log",
    DELETE: "/api/log",
    BY_ACCOUNT: "/api/log",
  },

  // 북마크 관련
  BOOKMARK: {
    LIST: "/api/bookmarks",
    CREATE: "/api/bookmarks",
    DELETE: "/api/bookmarks",
    UPDATE: "/api/bookmarks",
  },

  // 음성 인식 관련
  VOICE: {
    RECOGNIZE: "/api/voice",
    WEBSOCKET: "/api/voice/websocket",
  },

  // 뉴스 관련
  NEWS: {
    LIST: "/api/news",
    DETAIL: "/api/news/detail",
    SEARCH: "/api/news/search",
  },
};
