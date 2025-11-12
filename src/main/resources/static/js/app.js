// 공통 API 유틸
const Api = (() => {
    const getAccess = () => localStorage.getItem('accessToken');
    const getRefresh = () => localStorage.getItem('refreshToken');
    const setAccess = (t) => localStorage.setItem('accessToken', t);
    const clearTokens = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    };

    // 기본 요청: Authorization 자동 첨부
    async function request(input, init = {}, retry = true) {
        const headers = new Headers(init.headers || {});
        const token = getAccess();
        if (token) headers.set('Authorization', `Bearer ${token}`);
        if (!headers.has('Content-Type') && !(init?.body instanceof FormData)) {
            headers.set('Content-Type', 'application/json');
        }

        const resp = await fetch(input, { ...init, headers });

        // 정상 또는 401 외 상태는 그대로 반환
        if (resp.status !== 401) return resp;

        // 이미 재시도 했거나 refresh 토큰 없음 -> 그대로 반환
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

    // 파일 업로드
    async function upload(url, formData) {
        const resp = await request(url, { method: 'POST', body: formData, headers: {} });
        if (!resp.ok) throw await buildError(resp);
        return resp.json();
    }

    // 401용: 액세스 토큰 갱신
    async function refreshAccessToken() {
        try {
            const resp = await fetch('/api/user/refresh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ refreshToken: getRefresh() }),
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
        } catch (e) {
            clearTokens();
            return false;
        }
    }

    async function buildError(resp) {
        let payload = null;
        try { payload = await resp.json(); } catch (_) {}
        const err = new Error(payload?.message || `HTTP ${resp.status}`);
        err.status = resp.status;
        err.payload = payload;
        return err;
    }

    async function getJson(url, opts={}) {
        const token = getAccessToken();
        const headers = { 'Content-Type': 'application/json', ...(opts.headers||{}) };
        if (token) headers.Authorization = `Bearer ${token}`;
        const res = await fetch(url, { ...opts, headers, credentials: 'same-origin' });
        if (res.status === 401) {
            const ok = await tryRefresh(); // /api/user/refresh 호출
            if (ok) return getJson(url, opts);
            // 실패 시 로그인 이동
            window.location.href = '/auth/login';
            throw Object.assign(new Error('Unauthorized'), { status: 401 });
        }
        if (!res.ok) throw Object.assign(new Error('HTTP '+res.status), { status: res.status });
        return res.json();
    }

    return { request, getJson, postJson, upload };
})();
window.Api = Api;
