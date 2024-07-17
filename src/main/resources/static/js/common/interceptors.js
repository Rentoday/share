// interceptors.js
import axios from 'axios';

const instance = axios.create();

// 요청 인터셉터
instance.interceptors.request.use(
  async (config) => {
    let token = localStorage.getItem('Authorization');

    if (token) {
      config.headers['Authorization'] = token;
      return config;
    }

    const authCookie = document.cookie.split(';').find(cookie => cookie.trim().startsWith('Authorization='));

    if (authCookie) {
      document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    }

    try {
      const response = await axios.get('/api/token/reissue', {
        withCredentials: true
      });

      token = response.headers['authorization'];
      localStorage.setItem('Authorization', token);
      config.headers['Authorization'] = token;
    } catch (error) {
      console.error('Token reissue failed:', error);
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
instance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    if (error.response.status === 400 && error.response.data === "만료된 AccessToken입니다. 재발급해주세요." && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const response = await axios.get('/api/token/reissue', {
          withCredentials: true
        });

        const token = response.headers['authorization'];
        localStorage.setItem('Authorization', token);
        axios.defaults.headers.common['Authorization'] = token;
        originalRequest.headers['Authorization'] = token;

        return instance(originalRequest);
      } catch (error) {
        console.error('Token reissue failed:', error);
      }
    }

    return Promise.reject(error);
  }
);

export default instance;