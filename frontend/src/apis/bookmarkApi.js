import { api } from './apiUtils';
import { API_ENDPOINTS } from './config';

export const bookmarkApi = {
  // 북마크 목록 조회
  getBookmarkList: async (params = {}) => {
    try {
      const response = await api.get(API_ENDPOINTS.BOOKMARK.LIST, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 목록 조회에 실패했습니다.');
    }
  },

  // 북마크 생성
  createBookmark: async (bookmarkData) => {
    try {
      const response = await api.post(API_ENDPOINTS.BOOKMARK.CREATE, bookmarkData);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 생성에 실패했습니다.');
    }
  },

  // 북마크 수정
  updateBookmark: async (bookmarkId, updateData) => {
    try {
      const response = await api.put(`${API_ENDPOINTS.BOOKMARK.UPDATE}/${bookmarkId}`, updateData);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 수정에 실패했습니다.');
    }
  },

  // 북마크 삭제
  deleteBookmark: async (bookmarkId) => {
    try {
      const response = await api.delete(`${API_ENDPOINTS.BOOKMARK.DELETE}/${bookmarkId}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 삭제에 실패했습니다.');
    }
  },

  // 북마크 상세 조회
  getBookmarkDetail: async (bookmarkId) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BOOKMARK.LIST}/${bookmarkId}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 상세 조회에 실패했습니다.');
    }
  },

  // 북마크 검색
  searchBookmarks: async (searchParams) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BOOKMARK.LIST}/search`, searchParams);
      return response;
    } catch (error) {
      throw new Error(error.message || '북마크 검색에 실패했습니다.');
    }
  },

  // 북마크 카테고리별 조회
  getBookmarksByCategory: async (category) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.BOOKMARK.LIST}/category/${category}`);
      return response;
    } catch (error) {
      throw new Error(error.message || '카테고리별 북마크 조회에 실패했습니다.');
    }
  }
}; 