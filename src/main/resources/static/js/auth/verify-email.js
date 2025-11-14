document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('verifyEmailForm');
    const alertArea = document.getElementById('alertArea');
    const resendBtn = document.getElementById('resendBtn');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const userNum = document.getElementById('userNum').value;
        const email = document.getElementById('realEmail').value;
        const verificationCode = document.getElementById('verificationCode').value.trim();

        if (!verificationCode) {
            showAlert('인증번호를 입력해주세요', 'warning');
            return;
        }

        try {
            const response = await fetch('/api/auth/verify-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    userNum,
                    verificationCode
                })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('인증이 완료되었습니다', 'success');
                setTimeout(() => {
                    window.location.href = `/auth/reset-password?token=${data.token}`;
                }, 1000);
            } else {
                showAlert(data.message || '인증번호가 올바르지 않습니다', 'danger');
            }
        } catch (error) {
            console.error('Error:', error);
            showAlert('서버 오류가 발생했습니다', 'danger');
        }
    });

    // 재발송 버튼
    resendBtn.addEventListener('click', async function() {
        const userNum = document.getElementById('userNum').value;
        const userName = document.getElementById('userName').value;

        try {
            const response = await fetch('/api/auth/verify-user', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userNum, userName })
            });

            if (response.ok) {
                showAlert('인증번호가 재발송되었습니다', 'success');
            } else {
                showAlert('재발송에 실패했습니다', 'danger');
            }
        } catch (error) {
            showAlert('서버 오류가 발생했습니다', 'danger');
        }
    });

    function showAlert(message, type) {
        alertArea.innerHTML = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    }
});
