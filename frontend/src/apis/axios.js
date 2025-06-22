// src/api/axios.js
import axios from 'axios';

const instance = axios.create({
    baseURL: '/api', //  모든 요청에 /api 자동 접두사
    withCredentials: true, // 필요 시 쿠키 포함
    headers: {
        'Content-Type': 'application/json'
    }
});

export default instance;
