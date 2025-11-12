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

    const studentNum = document.getElementById('studentNum').value.trim();
    const password = document.getElementById('password').value;

    // 유효성 검사
    if (!studentNum || !password) {
        showAlert('학번과 비밀번호를 입력해주세요.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userNum: studentNum, password })
        });

        const data = await response.json();

        if (response.ok) {
            if (data.accessToken) localStorage.setItem('accessToken', data.accessToken);
            if (data.refreshToken) localStorage.setItem('refreshToken', data.refreshToken);
            showAlert('로그인 성공!', 'success');
            setTimeout(() => { window.location.href = '/main'; }, 500);
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
    let message = data?.message || '로그인에 실패했습니다.';
    showAlert(message, 'error');
}

function showAlert(message, type = 'info') {
    if (window.Utils?.showAlert) window.Utils.showAlert(message, type);
    else alert(message);
}
