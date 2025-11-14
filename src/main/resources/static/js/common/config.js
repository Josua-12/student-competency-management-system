// static/js/common/config.js
const CONFIG = {
    API: {
        BASE_URL: '/api',
        TIMEOUT: 10000,
        ENDPOINTS: {
            USER_INFO: '/api/user/info',
            PASSWORD_CHANGE: '/api/user/password',
            LOGIN: '/auth/login'
        }
    },

    MESSAGES: {
        SUCCESS: {
            USER_UPDATE: '사용자 정보가 성공적으로 수정되었습니다.',
            PASSWORD_CHANGE: '비밀번호가 성공적으로 변경되었습니다.',
            CANCEL_CONFIRM: '변경사항을 취소하시겠습니까?'
        },
        ERROR: {
            NETWORK: '네트워크 오류가 발생했습니다.',
            TIMEOUT: '요청 시간이 초과되었습니다.',
            AUTH_EXPIRED: '인증이 만료되었습니다.',
            VALIDATION: '입력값을 확인해주세요.',
            EMAIL_REQUIRED: '이메일은 필수 입력 항목입니다.',
            PASSWORD_MISMATCH: '새 비밀번호와 확인 비밀번호가 일치하지 않습니다.',
            PASSWORD_PATTERN: '비밀번호는 8자 이상, 대문자, 소문자, 숫자를 포함해야 합니다.'
        }
    },

    VALIDATION: {
        PASSWORD_PATTERN: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/,
        EMAIL_PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        PHONE_PATTERN: /^01[0-9]-?[0-9]{4}-?[0-9]{4}$/
    },

    UI: {
        DEBOUNCE_DELAY: 300,
        MESSAGE_TIMEOUT: 5000,
        LOADING_TEXT: '처리 중...'
    }
};
