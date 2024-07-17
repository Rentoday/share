// sseModule.js

class SSEModule {
    constructor() {
        this.eventSource = null;
        this.isConnected = false;
    }

    async connect() {
        if (this.isConnected) {
            console.log('SSE is already connected');
            return;
        }

        await this.setupSSEConnection();
    }

    async setupSSEConnection() {
        const token = localStorage.getItem('Authorization');
        if (!token) {
            console.error('No token found');
            return;
        }

        try {
            await this.refreshToken(token);
            this.createEventSource();
        } catch (error) {
            console.error('Failed to setup SSE connection:', error);
        }
    }

    async refreshToken(token) {
        const headers = new Headers({
            'Authorization': token
        });

        const response = await fetch('/api/token/reissue', {
            method: 'GET',
            headers: headers
        });

        if (response.status === 400 && await response.text() === "만료된 AccessToken입니다. 재발급해주세요.") {
            await this.requestNewToken();
        } else if (!response.ok) {
            throw new Error('Token refresh failed');
        }
    }

    async requestNewToken() {
        const response = await fetch('/api/token/reissue', {
            method: 'GET',
            credentials: 'include'  // to send httpOnly cookies
        });

        if (response.ok) {
            const newToken = response.headers.get('Authorization');
            if (newToken) {
                localStorage.setItem('Authorization', newToken);
                await this.refreshToken(newToken);
            } else {
                throw new Error('New token not received');
            }
        } else {
            throw new Error('New token request failed');
        }
    }

    createEventSource() {
        const token = localStorage.getItem('Authorization');
        const url = new URL('/api/notification/subscribe', window.location.origin);
        url.searchParams.append('authorization', token);

        this.eventSource = new EventSource(url);

        this.eventSource.onopen = () => {
            this.isConnected = true;
            console.log('SSE connection opened');
        };

        this.eventSource.onerror = (error) => {
            console.error('SSE connection error:', error);
            this.isConnected = false;
            this.eventSource.close();
        };

        this.eventSource.onmessage = (event) => {
            const data = JSON.parse(event.data);
            console.log('Received SSE message:', data);
            // 여기서 수신된 메시지를 처리합니다.
            // 예: this.handleNotification(data);
        };
    }

    disconnect() {
        if (this.eventSource) {
            this.eventSource.close();
            this.isConnected = false;
            console.log('SSE connection closed');
        }
    }

    // 알림 처리 메서드 (필요에 따라 구현)
    handleNotification(data) {
        // 알림 처리 로직
    }
}

// 싱글톤 인스턴스 생성 및 내보내기
const sseInstance = new SSEModule();
export default sseInstance;