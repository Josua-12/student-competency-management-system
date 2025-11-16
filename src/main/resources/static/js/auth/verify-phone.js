document.addEventListener('DOMContentLoaded', () => {
    const verifyBtn = document.getElementById('verifyBtn');
    const alertArea = document.getElementById('alertArea');
    const userNum = document.getElementById('userNum').value.trim();
    const phoneInput = document.getElementById('phoneInput');
    const phone = phoneInput ? phoneInput.value.trim() : '';

    // 인증 상태 폴링 간격(ms)
    const POLL_INTERVAL = 5000;

    async function checkVerificationStatus() {
        try {
            const response = await fetch(`/api/auth/check-verification?userNum=${encodeURIComponent(userNum)}&phone=${encodeURIComponent(phone)}`, {
                method: 'GET'
            });
            const data = await response.json();

            if (data.verified) {
                showAlert('인증이 완료되었습니다.', 'success');
                // 인증 완료 후 비밀번호 변경 페이지로 이동
                setTimeout(() => {
                    window.location.href = `/auth/reset-password?token=${data.token}`;
                }, 1500);
            } else {
                showAlert('인증 대기 중입니다. 인증 메시지를 확인해주세요.', 'info');
            }
        } catch (error) {
            console.error('인증 상태 조회 실패:', error);
            showAlert('인증 상태를 확인할 수 없습니다.', 'danger');
        }
    }

    function showAlert(message, type) {
        alertArea.innerHTML = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
    }

    // 초기 실행
    checkVerificationStatus();

    // 인증 상태 주기적 폴링 시작
    setInterval(checkVerificationStatus, POLL_INTERVAL);

// 인증 요청버튼(있으면) 클릭
