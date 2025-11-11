/**
 * 공통 유틸리티 및 CSRF 설정
 */

// CSRF 토큰 설정
document.addEventListener('DOMContentLoaded', function() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    // 모든 AJAX 요청에 사용될 수 있도록 window 객체에 저장
    if (token && header) {
        window.CSRF_TOKEN = token;
        window.CSRF_HEADER = header;
    }
});

/**
 * 전역 유틸리티 객체
 */
window.Utils = {
    /**
     * HTML 이스케이프 - XSS 방지
     */
    escapeHtml: function(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    /**
     * 날짜 포맷팅
     */
    formatDate: function(dateString, format = 'yyyy.MM.dd') {
        if (!dateString) return '';

        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return format
            .replace('yyyy', year)
            .replace('MM', month)
            .replace('dd', day)
            .replace('HH', hours)
            .replace('mm', minutes);
    },

    /**
     * CSRF 토큰과 함께 API 호출
     */
    apiCall: function(url, options = {}) {
        const config = {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        };

        // CSRF 토큰 추가
        if (window.CSRF_TOKEN && window.CSRF_HEADER) {
            config.headers[window.CSRF_HEADER] = window.CSRF_TOKEN;
        }

        try {
            const response = await fetch(url, config);

            // HTTP 상태 코드 체크 (404, 500 등)
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
            }

            return response;
        } catch (error) {
            // 네트워크 에러 또는 CORS 에러
            console.error('API 호출 실패 :', error);
            throw error;
        }
    },

    /**
    *  JSON 응답을 포함한 API 호출
    */
    apiCallJson: async function(url, options = {}) {
        const response = await this.apiCall(url, options);
        return await response.json();
    },

    /**
     * 성공 알림
     */
    showAlert: function(message, type = 'success') {
        const existingAlter = document.querySelector('.alert.custom-alert');
        if (existingAlter) {
            existingAlter.remove();
        }

        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.setAttribute('role', 'alert');
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            mainContent.insertBefore(alertDiv, mainContent.firstChild);

            // 5초 후 자동 제거
            setTimeout(() => {
                alertDiv.classList.remove('show');
                setTimeout(() => alertDiv.remove(), 150);
            }, 5000);
        }
    },

    /*
        로딩 표시
    */
    showLoading: function(show = true) {
        let spinner = document.querySelector('.global-spinner');

        if (show) {
            if (!spinner) {
                spinner = document.createElement('div');
                spinner.className = 'global-spinner';
                spinner.innerHTML = `
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">로딩 중...</span>
                                    </div>
                                `;
                document.body.appendChild(spinner);
            }
            spinner.style.display = 'flex';
        } else if (spinner) {
            spinner.style.display = 'none';
        }
    }
};
