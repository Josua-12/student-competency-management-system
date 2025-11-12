// JWT 토큰을 모든 요청에 자동으로 추가
(function() {
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        const token = localStorage.getItem('accessToken');
        if (token && args[1]) {
            args[1].headers = args[1].headers || {};
            args[1].headers['Authorization'] = `Bearer ${token}`;
        } else if (token) {
            args[1] = {
                ...args[1],
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            };
        }
        return originalFetch.apply(this, args);
    };
})();

// 페이지 로드 시 토큰 확인
window.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('accessToken');
    const currentPath = window.location.pathname;
    
    if (!token && !currentPath.startsWith('/auth/') && currentPath !== '/') {
        window.location.href = '/auth/login';
    }
});
