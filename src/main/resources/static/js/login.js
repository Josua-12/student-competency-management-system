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

    if (!studentNum || !password) {
        showAlert('학번과 비밀번호를 입력해주세요.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userNum: parseInt(studentNum),
                password: String(password)
            })

        });

        const data = await safeJson(response);

        if (response.ok) {
            persistTokens(data);
            showAlert('로그인 성공!', 'success');
            window.location.replace('/user/dashboard');
        } else {
            handleLoginError(data);
        }
    } catch (error) {
        console.error('로그인 오류:', error);
        showAlert('서버 연결에 실패했습니다.', 'error');
    }
}

/**
 * 응답 JSON 안전 파싱
 */
async function safeJson(resp) {
    try { return await resp.json(); } catch (_) { return null; }
}

/**
 * 토큰 저장
 */
function persistTokens(data) {
    console.log('토큰 저장:', data);
    if (data?.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
        document.cookie = `accessToken=${data.accessToken}; path=/; samesite=strict`;
    }
    if (data?.refreshToken) {
        localStorage.setItem('refreshToken', data.refreshToken);
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
