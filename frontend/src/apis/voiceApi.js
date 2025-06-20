import { api } from './apiUtils';
import { API_ENDPOINTS } from './config';

export const voiceApi = {
  // 음성 인식 요청
  recognizeVoice: async (audioData) => {
    try {
      const response = await api.post(API_ENDPOINTS.VOICE.RECOGNIZE, audioData);
      return response;
    } catch (error) {
      throw new Error(error.message || '음성 인식에 실패했습니다.');
    }
  },

  // WebSocket 연결 URL 가져오기
  getWebSocketUrl: () => {
    const baseUrl = API_ENDPOINTS.VOICE.WEBSOCKET.replace('/api', '');
    return `ws://localhost:8080${baseUrl}`;
  },

  // 음성 합성 요청
  synthesizeSpeech: async (textData) => {
    try {
      const response = await api.post(`${API_ENDPOINTS.VOICE.RECOGNIZE}/synthesize`, textData);
      return response;
    } catch (error) {
      throw new Error(error.message || '음성 합성에 실패했습니다.');
    }
  },

  // 음성 설정 조회
  getVoiceSettings: async () => {
    try {
      const response = await api.get(`${API_ENDPOINTS.VOICE.RECOGNIZE}/settings`);
      return response;
    } catch (error) {
      throw new Error(error.message || '음성 설정 조회에 실패했습니다.');
    }
  },

  // 음성 설정 업데이트
  updateVoiceSettings: async (settings) => {
    try {
      const response = await api.put(`${API_ENDPOINTS.VOICE.RECOGNIZE}/settings`, settings);
      return response;
    } catch (error) {
      throw new Error(error.message || '음성 설정 업데이트에 실패했습니다.');
    }
  },

  // 음성 인식 히스토리 조회
  getRecognitionHistory: async (params = {}) => {
    try {
      const response = await api.get(`${API_ENDPOINTS.VOICE.RECOGNIZE}/history`, params);
      return response;
    } catch (error) {
      throw new Error(error.message || '음성 인식 히스토리 조회에 실패했습니다.');
    }
  }
}; 