// static/js/common/error-handler.js
class ErrorHandler {
    static init() {
        // 전역 에러 핸들러 설정
        window.addEventListener('unhandledrejection', (event) => {
            this.handleGlobalError(event.reason);
        });
    }

    static handleGlobalError(error) {
        if (error?.status === 401 || error?.message?.includes('401')) {
            ApiUtils.clearToken();
            window.location.href = '/auth/login';
            return;
        }
        console.error('Global error:', error);
    }

    static classifyError(error) {
        if (error.name === 'AbortError') return 'TIMEOUT';
        if (!navigator.onLine) return 'OFFLINE';
        if (error.message.includes('401')) return 'AUTH_EXPIRED';
        if (error.message.includes('403')) return 'FORBIDDEN';
        if (error.message.includes('500') || error.message.includes('503')) return 'SERVER_ERROR';
        if (error.message.includes('400')) return 'VALIDATION_ERROR';
        return 'NETWORK_ERROR';
    }

    static getErrorMessage(error) {
        const errorType = this.classifyError(error);

        switch (errorType) {
            case 'TIMEOUT': return CONFIG.MESSAGES.ERROR.TIMEOUT;
            case 'OFFLINE': return '인터넷 연결을 확인해주세요.';
            case 'AUTH_EXPIRED': return CONFIG.MESSAGES.ERROR.AUTH_EXPIRED;
            case 'FORBIDDEN': return '접근 권한이 없습니다.';
            case 'SERVER_ERROR': return '서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.';
            case 'VALIDATION_ERROR': return error.message || CONFIG.MESSAGES.ERROR.VALIDATION;
            default: return error.message || CONFIG.MESSAGES.ERROR.NETWORK;
        }
    }

    static showError(error, elementId = 'alertMessage') {
        const message = this.getErrorMessage(error);
        const alertElement = document.getElementById(elementId);

        if (alertElement) {
            alertElement.className = 'alert alert-danger';
            alertElement.textContent = message;
            alertElement.classList.remove('d-none');
        } else {
            alert(message);
        }
        console.error('Error:', error);
    }

    static showSuccess(message, elementId = 'alertMessage') {
        const alertElement = document.getElementById(elementId);

        if (alertElement) {
            alertElement.className = 'alert alert-success';
            alertElement.textContent = message;
            alertElement.classList.remove('d-none');

            setTimeout(() => {
                alertElement.classList.add('d-none');
            }, CONFIG.UI.MESSAGE_TIMEOUT);
        } else {
            alert(message);
        }
    }
}
