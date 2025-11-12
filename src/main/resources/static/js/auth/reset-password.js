/**
 * 비밀번호 재설정
 */

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('resetPasswordForm');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');
    const alertArea = document.getElementById('alertArea');

    // 비밀번호 표시/숨김
    togglePasswordBtn.addEventListener('click', function() {
        const type = newPasswordInput.type === 'password' ? 'text' : 'password';
        newPasswordInput.type = type;
        confirmPasswordInput.type = type;

        const icon = this.querySelector('i');
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    });

    // 비밀번호 강도 체크
    newPasswordInput.addEventListener('input', function() {
        const password = this.value;
        checkPasswordStrength(password);
    });

    // 비밀번호 확인 체크
    confirmPasswordInput.addEventListener('input', function() {
        if (newPasswordInput.value !== this.value) {
            this.classList.add('is-invalid');
        } else {
            this.classList.remove('is-invalid');
        }
    });

    // 폼 제출
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const token = document.getElementById('token').value;
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // 유효성 검사
        if (newPassword.length < 8) {
            showAlert('비밀번호는 8자 이상이어야 합니다', 'warning');
            return;
        }

        if (newPassword !== confirmPassword) {
            showAlert('비밀번호가 일치하지 않습니다', 'warning');
            return;
        }

        if (!isPasswordValid(newPassword)) {
            showAlert('비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다', 'warning');
            return;
        }

        try {
            const response = await fetch('/api/auth/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: token,
                    newPassword: newPassword
                })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('비밀번호가 변경되었습니다', 'success');
                setTimeout(() => {
                    window.location.href = '/auth/login';
                }, 2000);
            } else {
                showAlert(data.message || '비밀번호 변경에 실패했습니다', 'danger');
            }
        } catch (error) {
            console.error('Error:', error);
            showAlert('서버 오류가 발생했습니다', 'danger');
        }
    });

    // 비밀번호 강도 체크 함수
    function checkPasswordStrength(password) {
        let strength = 0;
        let text = '';

        if (password.length >= 8) strength++;
        if (password.match(/[a-z]/)) strength++;
        if (password.match(/[A-Z]/)) strength++;
        if (password.match(/[0-9]/)) strength++;
        if (password.match(/[^a-zA-Z0-9]/)) strength++;

        strengthBar.classList.remove('weak', 'medium', 'strong');

        if (strength <= 2) {
            strengthBar.classList.add('weak');
            text = '약함';
        } else if (strength <= 4) {
            strengthBar.classList.add('medium');
            text = '보통';
        } else {
            strengthBar.classList.add('strong');
            text = '강함';
        }

        strengthText.textContent = text;
    }

    // 비밀번호 유효성 검사
    function isPasswordValid(password) {
        const hasLetter = /[a-zA-Z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasSpecial = /[^a-zA-Z0-9]/.test(password);

        return hasLetter && hasNumber && hasSpecial;
    }

    // 알림 표시
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
