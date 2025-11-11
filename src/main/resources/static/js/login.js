/**
 * 로그인 페이지 JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

/**
 * 로그인 처리
 */
async function handleLogin(e) {
    e.preventDefault();

    const studentNum = document.getElementById('studentNum').value;
    const password = document.getElementById('password').value;

    // 유효성 검사
    if (!studentNum || !password) {
        showAlert('학번과 비밀번호를 입력해주세요.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                studentNum: parseInt(studentNum),
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {
            // 토큰 저장
            localStorage.setItem('accessToken', data.accessToken);
            if (data.refreshToken) {
                localStorage.setItem('refreshToken', data.refreshToken);
            }

            // 메인 페이지로 이동
            showAlert('로그인 성공!', 'success');
            setTimeout(() => {
                window.location.href = '/main';
            }, 500);
        } else {
            // 에러 처리
            handleLoginError(data);
        }

    } catch (error) {
        console.error('로그인 오류:', error);
        showAlert('서버 연결에 실패했습니다.', 'error');
    }
}

/**
 * 로그인 에러 처리
 */
function handleLoginError(data) {
    let message = '로그인에 실패했습니다.';

    if (data.code === 'ACCOUNT_LOCKED') {
        message = '계정이 잠겼습니다. 잠시 후 다시 시도해주세요.';
    } else if (data.code === 'INVALID_CREDENTIALS') {
        message = '학번 또는 비밀번호가 올바르지 않습니다.';
    } else if (data.message) {
        message = data.message;
    }

    showAlert(message, 'error');
}

/**
 * 알림 메시지 표시
 */
function showAlert(message, type = 'info') {
    // app.js의 Utils.showAlert 사용
    if (window.Utils && window.Utils.showAlert) {
        window.Utils.showAlert(message, type);
    } else {
        alert(message);
    }
}
