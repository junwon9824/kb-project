import { api, tokenManager } from "./apiUtils";
import { API_ENDPOINTS } from "./config";

export const userApi = {
  // 로그인
  login: async (loginData) => {
    try {
      const response = await api.post(API_ENDPOINTS.USER.LOGIN, loginData);

      if (response.token) {
        console.log("settoken", response.token);
        tokenManager.setToken(response.token);
      }

      return response;
    } catch (error) {
      throw new Error(error.message || "로그인에 실패했습니다.");
    }
  },

  // 회원가입
  signup: async (signupData) => {
    try {
      const response = await api.post(API_ENDPOINTS.USER.SIGNUP, signupData);
      return response;
    } catch (error) {
      throw new Error(error.message || "회원가입에 실패했습니다.");
    }
  },

  // 로그아웃
  logout: async () => {
    try {
      await api.post(API_ENDPOINTS.USER.LOGOUT);
      tokenManager.removeToken();
      return { success: true };
    } catch (error) {
      // 서버 에러가 있어도 토큰은 제거
      tokenManager.removeToken();
      throw new Error(error.message || "로그아웃에 실패했습니다.");
    }
  },

  // 사용자 프로필 조회
  getProfile: async () => {
    try {
      const response = await api.get(API_ENDPOINTS.USER.PROFILE);
      return response;
    } catch (error) {
      throw new Error(error.message || "프로필 조회에 실패했습니다.");
    }
  },

  // 인증 상태 확인
  checkAuth: () => {
    return tokenManager.isAuthenticated();
  },
};
