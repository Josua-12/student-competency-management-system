// static/js/common/api-utils.js
class ApiUtils {
    static BASE_URL = CONFIG.API.BASE_URL;
    static TIMEOUT = CONFIG.API.TIMEOUT;
    static #token = null;

    static setToken(token) {
        this.#token = token;
    }

    static getToken() {
        if (!this.#token) {
            this.redirectToLogin();
            return null;
        }

        if (this.isTokenExpired(this.#token)) {
            this.clearToken();
            this.redirectToLogin();
            return null;
        }

        return this.#token;
    }

    static isTokenExpired(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch (e) {
            return true;
        }
    }

    static clearToken() {
        this.#token = null;
    }

    static redirectToLogin() {
        window.location.href = '/auth/login';
    }

    static async handleResponse(response) {
        if (response.status === 401) {
            this.clearToken();
            this.redirectToLogin();
            throw new Error(CONFIG.MESSAGES.ERROR.AUTH_EXPIRED);
        }

        if (response.status === 403) {
            throw new Error('접근 권한이 없습니다.');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `서버 오류 (${response.status})`);
        }

        return response;
    }

    static async request(url, options = {}) {
        const token = this.getToken();
        if (!token) return;

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), this.TIMEOUT);

        try {
            const response = await fetch(this.BASE_URL + url, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                    ...options.headers
                },
                signal: controller.signal
            });

            clearTimeout(timeoutId);
            return await this.handleResponse(response);
        } catch (error) {
            clearTimeout(timeoutId);
            if (error.name === 'AbortError') {
                throw new Error(CONFIG.MESSAGES.ERROR.TIMEOUT);
            }
            throw error;
        }
    }
}
