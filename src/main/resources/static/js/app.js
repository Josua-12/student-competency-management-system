// 공통 API 유틸
const Api = (() => {
    // LocalStorage 토큰 유틸
    const getAccess = () => localStorage.getItem('accessToken');
    const getRefresh = () => localStorage.getItem('refreshToken');
    const setAccess = (t) => localStorage.setItem('accessToken', t);
    const clearTokens = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    };

    // 401 처리 포함 공통 request
    async function request(input, init = {}, retry = true) {
        const headers = new Headers(init.headers || {});
        const token = getAccess();
        if (token) headers.set('Authorization', `Bearer ${token}`);
        if (!headers.has('Content-Type') && !(init?.body instanceof FormData)) {
            headers.set('Content-Type', 'application/json');
        }

        const resp = await fetch(input, { ...init, headers, credentials: 'same-origin' });

        // 401 외 상태는 그대로 반환
        if (resp.status !== 401) return resp;

        // 재시도 불가 또는 리프레시 없음
        if (!retry || !getRefresh()) return resp;

        // 액세스 토큰 갱신 시도
        const refreshed = await refreshAccessToken();
        if (!refreshed) return resp;

        // 새 토큰으로 1회 재시도
        return request(input, init, false);
    }

    // JSON GET
    async function getJson(url) {
        const resp = await request(url, { method: 'GET' });
        if (!resp.ok) throw await buildError(resp);
        return resp.json();
    }

    // JSON POST
    async function postJson(url, body) {
        const resp = await request(url, { method: 'POST', body: JSON.stringify(body) });
        if (!resp.ok) throw await buildError(resp);
        return resp.json();
    }

    // 파일 업로드 (FormData 사용시 Content-Type 자동)
    async function upload(url, formData) {
        const resp = await request(url, { method: 'POST', body: formData, headers: {} });
        if (!resp.ok) throw await buildError(resp);
        return resp.json();
    }

    // 401 시 토큰 갱신
    async function refreshAccessToken() {
        try {
            const refreshToken = getRefresh();
            if (!refreshToken) return false;

            const resp = await fetch('/api/user/refresh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken }),
                credentials: 'same-origin',
            });
            if (!resp.ok) {
                clearTokens();
                return false;
            }
            const data = await resp.json();
            if (data?.accessToken) {
                setAccess(data.accessToken);
                return true;
            }
            clearTokens();
            return false;
        } catch (_) {
            clearTokens();
            return false;
        }
    }

    // 에러 객체 표준화
    async function buildError(resp) {
        let payload = null;
        try { payload = await resp.json(); } catch (_) {}
        const err = new Error(payload?.message || `HTTP ${resp.status}`);
        err.status = resp.status;
        err.payload = payload;
        return err;
    }

    return { request, getJson, postJson, upload };
})();

window.Api = Api;

// 모든 fetch 요청에 JWT 토큰 자동 추가
const originalFetch = window.fetch;
window.fetch = function(url, options = {}) {
    const token = localStorage.getItem('accessToken');
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
    }
    return originalFetch(url, options);
};

