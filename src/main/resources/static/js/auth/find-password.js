document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('findPasswordForm');
    const alertArea = document.getElementById('alertArea');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const userNum = document.getElementById('userNum').value.trim();
        const userName = document.getElementById('userName').value.trim();

        if (!userNum || !userName) {
            showAlert('학번과 이름을 모두 입력해주세요', 'warning');
            return;
        }

        try {
            const response = await fetch('/api/auth/verify-user', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ userNum, userName })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('사용자 확인 완료', 'success');
                setTimeout(() => {
                    window.location.href = `/auth/verify-phone?userNum=${encodeURIComponent(userNum)}`;
                }, 1000);
            } else {
                showAlert(data.message || '사용자 정보를 찾을 수 없습니다', 'danger');
            }
        } catch (error) {
            console.error('Error:', error);
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

    document.querySelectorAll('input').forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                form.dispatchEvent(new Event('submit'));
            }
        });
    });
});
