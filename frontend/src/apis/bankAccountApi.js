// src/api/bankAccountApi.js
import { api } from "./apiUtils";
import { API_ENDPOINTS } from "./config";

export const bankAccountApi = {
  // 계좌 목록 조회
  getAccountList: async (userid) => {
    try {
      console.log("getaccountlist");
      const response = await api.get(`${API_ENDPOINTS.BANK_ACCOUNT.LIST}/user/${userid}`);
      console.log("aftergetaccountlist");

      return response;
    } catch (error) {
      console.error('계좌 목록 조회 실패:', error);
      throw error;
    }
  },

  // 계좌 상세 정보 조회
  getAccountDetail: async (accountId) => {
    try {
      const response = await api.get(
        `${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}/${accountId}`,
      );

      return response;
    } catch (error) {
      throw new Error(error.message || "계좌 상세 정보 조회에 실패했습니다.");
    }
  },

  // 계좌 이체
  transfer: async (transferData) => {
    try {
      const response = await api.post(
        `${API_ENDPOINTS.BANK_ACCOUNT.TRANSFER}`,
        transferData,
      );
      return response;
    } catch (error) {
      throw new Error(error.message || "계좌 이체에 실패했습니다.");
    }
  },

  // 계좌 잔액 조회
  getBalance: async (accountId) => {
    try {
      const response = await api.get(
        `${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}/${accountId}/balance`,
      );
      return response;
    } catch (error) {
      throw new Error(error.message || "잔액 조회에 실패했습니다.");
    }
  },

  // 거래 내역 조회
  getTransactionHistory: async (accountId, params = {}) => {
    try {
      const response = await api.get(
        `${API_ENDPOINTS.BANK_ACCOUNT.DETAIL}/${accountId}/transactions`,
        { params },
      );
      return response;
    } catch (error) {
      throw new Error(error.message || "거래 내역 조회에 실패했습니다.");
    }
  },

  // 계좌 생성
  createBankAccount: async (bankAccountData, userid) => {
    try {
      const response = await api.post(`${API_ENDPOINTS.BANK_ACCOUNT.CREATE}?userid=${userid}`, bankAccountData);
      return response.data;
    } catch (error) {
      console.error('계좌 생성 실패:', error);
      throw error;
    }
  },

  // 계좌 삭제
  deleteBankAccount: async (accountId) => {
    try {
      const response = await api.delete(`${API_ENDPOINTS.BANK_ACCOUNT.DELETE}/${accountId}`);
      return response.data;
    } catch (error) {
      console.error('계좌 삭제 실패:', error);
      throw error;
    }
  },

  // 주계좌 설정
  setMainAccount: async (accountId) => {
    try {
      const response = await api.put(`${API_ENDPOINTS.BANK_ACCOUNT.SET_MAIN}/${accountId}`);
      return response.data;
    } catch (error) {
      console.error('주계좌 설정 실패:', error);
      throw error;
    }
  }
};
