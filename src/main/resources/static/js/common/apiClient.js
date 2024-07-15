let isRefreshing = false;
let refreshSubscribers = [];

function onRefreshed(token) {
  refreshSubscribers.forEach(cb => cb(token));
  refreshSubscribers = [];
}

function addSubscriber(cb) {
  refreshSubscribers.push(cb);
}

class SSEClient {
    constructor(baseURL) {
        this.baseURL = baseURL;
        this.eventSource = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 1000; // 1초
    }

    connect() {
        if (this.eventSource) {
            this.eventSource.close();
        }

        const token = localStorage.getItem('Authorization');
        const url = `${this.baseURL}/api/notification/subscribe`;

        const fetchOptions = {
            method: 'GET',
            headers: {
                'Authorization': token,
                'Cache-Control': 'no-cache',
                'Connection': 'keep-alive'
            },
            credentials: 'include'  // 쿠키를 포함하여 요청을 보냅니다.
        };

        this.eventSource = new EventSourcePolyfill(url, fetchOptions);

        this.eventSource.onopen = () => {
            console.log('SSE connection opened');
            this.reconnectAttempts = 0;
        };

        this.eventSource.onerror = (error) => {
            console.error('SSE connection error:', error);
            this.eventSource.close();
            this.reconnect();
        };

        this.eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.handleMessage(data);
        };
    }

    reconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            setTimeout(() => this.connect(), this.reconnectInterval);
        } else {
            console.error('Max reconnect attempts reached. Please try again later.');
        }
    }

    handleMessage(data) {
        console.log('Received message:', data);
        // 여기에서 메시지 유형에 따른 처리를 구현합니다.
        // 예: 알림 표시, UI 업데이트 등
    }

    close() {
        if (this.eventSource) {
            this.eventSource.close();
        }
    }
}

// EventSource 폴리필
class EventSourcePolyfill {
    constructor(url, options = {}) {
        this.url = url;
        this.options = options;
        this.eventListeners = {};
        this.readyState = 0;
        this.connect();
    }

    connect() {
        this.readyState = 0;
        this.fetchEventSource();
    }

    async fetchEventSource() {
        try {
            const response = await fetch(this.url, this.options);
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let buffer = '';

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                buffer += decoder.decode(value, { stream: true });
                const lines = buffer.split('\n');
                buffer = lines.pop();

                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        const event = { data: line.slice(5).trim() };
                        this.dispatchEvent('message', event);
                    }
                }
            }
        } catch (error) {
            this.dispatchEvent('error', error);
        }
    }

    addEventListener(type, callback) {
        if (!this.eventListeners[type]) {
            this.eventListeners[type] = [];
        }
        this.eventListeners[type].push(callback);
    }

    removeEventListener(type, callback) {
        if (this.eventListeners[type]) {
            this.eventListeners[type] = this.eventListeners[type].filter(cb => cb !== callback);
        }
    }

    dispatchEvent(type, event) {
        if (this.eventListeners[type]) {
            for (const callback of this.eventListeners[type]) {
                callback(event);
            }
        }
        if (type === 'message' && this.onmessage) {
            this.onmessage(event);
        } else if (type === 'error' && this.onerror) {
            this.onerror(event);
        } else if (type === 'open' && this.onopen) {
            this.onopen(event);
        }
    }

    close() {
        this.readyState = 2;
    }
}

const api = axios.create({
  baseURL: 'http://localhost:81',
  withCredentials: true  // 쿠키를 포함하여 요청을 보냅니다.
});

// 요청 인터셉터
api.interceptors.request.use(
  async (config) => {
    let token = localStorage.getItem('Authorization');

    if (!token) {
      const authCookie = Cookies.get('Authorization');
      if (authCookie) {
        Cookies.remove('Authorization');
        token = await reissueToken();
      }
    }

    if (token) {
      config.headers['Authorization'] = token;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    const newToken = response.headers['authorization'];
    if (newToken) {
      localStorage.setItem('Authorization', newToken);
    }
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    if (error.response && error.response.status === 400 &&
        error.response.data.message === "만료된 AccessToken입니다. 재발급해주세요." &&
        !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise(resolve => {
          addSubscriber(token => {
            originalRequest.headers['Authorization'] = token;
            resolve(api(originalRequest));
          });
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const newToken = await reissueToken();
        originalRequest.headers['Authorization'] = newToken;
        return api(originalRequest);
      } catch (refreshError) {
        localStorage.removeItem('Authorization');
        // 로그인 페이지로 리다이렉트 또는 로그인 모달 표시
        window.location.href = '/login';  // 또는 적절한 로그인 페이지 URL
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

// 토큰 재발급 함수
async function reissueToken() {
  try {
    const response = await api.get('/api/token/reissue');
    const newToken = response.headers['authorization'];
    if (newToken) {
      localStorage.setItem('Authorization', newToken);
      return newToken;
    }
    throw new Error('New token not received');
  } catch (error) {
    console.error('Token reissue failed:', error);
    throw error;
  }
}

// API 요청 예시
async function someApiRequest() {
  try {
    const response = await api.get('/some-endpoint');
    return response.data;
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

// 전역 범위에 함수 노출 (필요한 경우)
window.someApiRequest = someApiRequest;