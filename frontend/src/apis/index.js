// index.js

import { userApi } from './userApi';
import { bankAccountApi } from './bankAccountApi';
import { logApi } from './logApi';
import { bookmarkApi } from './bookmarkApi';
import { voiceApi } from './voiceApi';
import { newsApi } from './newsApi';

export { API_CONFIG, API_ENDPOINTS } from './config';
export { api, apiClient, tokenManager } from './apiUtils';

export {
  userApi,
  bankAccountApi,
  logApi,
  bookmarkApi,
  voiceApi,
  newsApi,
};

export const apis = {
  user: userApi,
  bankAccount: bankAccountApi,
  log: logApi,
  bookmark: bookmarkApi,
  voice: voiceApi,
  news: newsApi,
};

