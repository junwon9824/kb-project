import { api } from './apiUtils';
import { API_ENDPOINTS } from './config';

export const newsApi = {
  // 뉴스 목록 조회
  getNewsList: async (params = {}) => {
    try {
      const response = await api.get(API_ENDPOINTS.NEWS.LIST, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '뉴스 목록 조회에 실패했습니다.');
    }
  },

  // 뉴스 상세 조회
  getNewsDetail: async (newsId) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.NEWS.DETAIL}/${newsId}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '뉴스 상세 조회에 실패했습니다.');
    }
  },

  // 뉴스 검색
  searchNews: async (searchParams) => {
    try {
      const response = await api.get(API_ENDPOINTS.NEWS.SEARCH, searchParams);
      return response;
    } catch (error) {
      throw new Error(error.message || '뉴스 검색에 실패했습니다.');
    }
  },

  // 카테고리별 뉴스 조회
  getNewsByCategory: async (category) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.NEWS.LIST}/category/${category}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '카테고리별 뉴스 조회에 실패했습니다.');
    }
  },

  // 인기 뉴스 조회
  getPopularNews: async (params = {}) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.NEWS.LIST}/popular`, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '인기 뉴스 조회에 실패했습니다.');
    }
  },

  // 최신 뉴스 조회
  getLatestNews: async (params = {}) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.NEWS.LIST}/latest`, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '최신 뉴스 조회에 실패했습니다.');
    }
  },

  // 뉴스 북마크 추가
  addNewsToBookmark: async (newsId) => {
    try {
      const response = await api.post(`${API_ENDPOINTS.NEWS.DETAIL}/${newsId}/bookmark`);
      return response;
    } catch (error) {
      throw new Error(error.message || '뉴스 북마크 추가에 실패했습니다.');
    }
  },

  // 뉴스 북마크 제거
  removeNewsFromBookmark: async (newsId) => {
    try {
      const response = await api.delete(`${API_ENDPOINTS.NEWS.DETAIL}/${newsId}/bookmark`);
      return response;
    } catch (error) {
      throw new Error(error.message || '뉴스 북마크 제거에 실패했습니다.');
    }
  }
}; 