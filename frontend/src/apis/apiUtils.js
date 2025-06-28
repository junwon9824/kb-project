import { API_CONFIG } from "./config";

// 기본 HTTP 클라이언트
class ApiClient {
  constructor() {
    this.baseURL = API_CONFIG.BASE_URL;
    this.timeout = API_CONFIG.TIMEOUT;
    this.defaultHeaders = API_CONFIG.HEADERS;
  }

  // 요청 헤더 설정
  getHeaders() {
    const headers = { ...this.defaultHeaders };

    // 로컬 스토리지에서 토큰 가져오기
    const token = localStorage.getItem("authToken");
    console.log("headers", token);
    
    // 토큰이 있으면 Authorization 헤더 추가
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }
    
    return headers;
  }

  // GET 요청
  async get(url, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const fullUrl = queryString ? `${url}?${queryString}` : url;

    try {
      const response = await fetch(`${this.baseURL}${fullUrl}`, {
        method: "GET",
        headers: this.getHeaders(),
        signal: AbortSignal.timeout(this.timeout),
      });
      console.log("this.getHeaders()", this.getHeaders());
      return await this.handleResponse(response);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // POST 요청
  async post(url, data = {}) {
    try {
      const response = await fetch(`${this.baseURL}${url}`, {
        method: "POST",
        headers: this.getHeaders(),
        body: JSON.stringify(data),
        signal: AbortSignal.timeout(this.timeout),
      });

      return await this.handleResponse(response);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // PUT 요청
  async put(url, data = {}) {
    try {
      const response = await fetch(`${this.baseURL}${url}`, {
        method: "PUT",
        headers: this.getHeaders(),
        body: JSON.stringify(data),
        signal: AbortSignal.timeout(this.timeout),
      });

      return await this.handleResponse(response);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // DELETE 요청
  async delete(url) {
    try {
      const response = await fetch(`${this.baseURL}${url}`, {
        method: "DELETE",
        headers: this.getHeaders(),
        signal: AbortSignal.timeout(this.timeout),
      });

      return await this.handleResponse(response);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  // 응답 처리
  async handleResponse(response) {
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));

      // 401(만료) 또는 403(권한 없음)일 때 토큰 삭제 후 로그인 페이지로 리다이렉트
      if (response.status === 401 || response.status === 403) {
        console.log("토큰 만료 또는 권한 없음 - 토큰 삭제 후 로그인 페이지로 이동");
        tokenManager.removeToken();
        window.location.href = "/users/login";
        return;
      }

      throw new Error(
        errorData.message || `HTTP error! status: ${response.status}`,
      );
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return await response.json();
    }

    return await response.text();
  }

  // 에러 처리
  handleError(error) {
    if (error.name === "AbortError") {
      return new Error("요청 시간이 초과되었습니다.");
    }

    if (error.name === "TypeError" && error.message.includes("fetch")) {
      return new Error("네트워크 연결을 확인해주세요.");
    }

    return error;
  }
}

// API 클라이언트 인스턴스 생성
export const apiClient = new ApiClient();

// 편의 함수들
export const api = {
  get: (url, params) => apiClient.get(url, params),
  post: (url, data) => apiClient.post(url, data),
  put: (url, data) => apiClient.put(url, data),
  delete: (url) => apiClient.delete(url),
};

// 토큰 관리
export const tokenManager = {
  setToken: (token) => {
    localStorage.setItem("authToken", token);
  },

  getToken: () => {
    return localStorage.getItem("authToken");
  },

  removeToken: () => {
    localStorage.removeItem("authToken");
  },

  isAuthenticated: () => {
    return !!localStorage.getItem("authToken");
  },
};
