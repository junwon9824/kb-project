import { api } from './apiUtils';
import { API_ENDPOINTS } from './config';

export const logApi = {
  // 로그 목록 조회
  getLogList: async (params = {}) => {
    try {
      const response = await api.get(API_ENDPOINTS.LOG.LIST, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 목록 조회에 실패했습니다.');
    }
  },

  // 계좌번호로 로그 목록 조회  
  getLogsByAccountNumber: async (accountNumber) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.LOG.BY_ACCOUNT}/${accountNumber}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 목록 조회에 실패했습니다.');
    }
  },

  // 로그 생성
  createLog: async (logData) => {
    try {
      const response = await api.post(API_ENDPOINTS.LOG.CREATE, logData);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 생성에 실패했습니다.');
    }
  },

  // 로그 삭제
  deleteLog: async (logId) => {
    try {
      const response = await api.delete(`${API_ENDPOINTS.LOG.DELETE}/${logId}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 삭제에 실패했습니다.');
    }
  },

  // 로그 상세 조회
  getLogDetail: async (logId) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.LOG.LIST}/${logId}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 상세 조회에 실패했습니다.');
    }
  },

  // 로그 검색
  searchLogs: async (searchParams) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.LOG.LIST}/search`, searchParams);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 검색에 실패했습니다.');
    }
  },

  // 로그 통계
  getLogStats: async () => {
    try {
      const response = await api.get(`${API_ENDPOINTS.LOG.LIST}/stats`);
      return response;
    } catch (error) {
      throw new Error(error.message || '로그 통계 조회에 실패했습니다.');
    }
  }
}; 