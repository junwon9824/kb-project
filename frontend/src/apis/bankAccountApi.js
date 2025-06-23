// src/api/bankAccountApi.js
import { api } from './apiUtils';
import { API_ENDPOINTS } from './config';

export const bankAccountApi = {
  // 계좌 목록 조회
  getAccountList: async () => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BANK_ACCOUNT.LIST}`);
      return response.data;
    } catch (error) {
      throw new Error(error.message || '계좌 목록 조회에 실패했습니다.');
    }
  },

  // 계좌 상세 정보 조회
  getAccountDetail: async (accountId) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}${accountId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.message || '계좌 상세 정보 조회에 실패했습니다.');
    }
  },

  // 계좌 이체
  transfer: async (transferData) => {
    try {
      const response = await api.post(`${API_ENDPOINTS.BANK_ACCOUNT.TRANSFER}`, transferData);
      return response.data;
    } catch (error) {
      throw new Error(error.message || '계좌 이체에 실패했습니다.');
    }
  },

  // 계좌 잔액 조회
  getBalance: async (accountId) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}${accountId}/balance`);
      return response.data;
    } catch (error) {
      throw new Error(error.message || '잔액 조회에 실패했습니다.');
    }
  },

  // 거래 내역 조회
  getTransactionHistory: async (accountId, params = {}) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}${accountId}/transactions`, { params });
      return response.data;
    } catch (error) {
      throw new Error(error.message || '거래 내역 조회에 실패했습니다.');
    }
  }
};




