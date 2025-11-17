document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('passwordChangeForm');
    const currentPassword = document.getElementById('currentPassword');
    const newPassword = document.getElementById('newPassword');
    const confirmPassword = document.getElementById('confirmPassword');
    const alertMessage = document.getElementById('alertMessage');
    const changeButton = document.getElementById('changeButton');
    const buttonText = document.getElementById('buttonText');
    const spinner = document.getElementById('spinner');

    function showAlert(message, type = 'danger') {
        alertMessage.className = `alert alert-${type}`;
        alertMessage.textContent = message;
        alertMessage.classList.remove('d-none');
    }

    function hideAlert() {
        alertMessage.classList.add('d-none');
    }

    function validatePasswords() {
        const newPwd = newPassword.value;
        const confirmPwd = confirmPassword.value;
        
        if (newPwd && confirmPwd && newPwd !== confirmPwd) {
            confirmPassword.setCustomValidity('비밀번호가 일치하지 않습니다.');
            return false;
        } else {
            confirmPassword.setCustomValidity('');
            return true;
        }
    }

    newPassword.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        hideAlert();

        if (!validatePasswords()) {
            showAlert('비밀번호가 일치하지 않습니다.');
            return;
        }

        // 로딩 상태
        changeButton.disabled = true;
        buttonText.classList.add('d-none');
        spinner.classList.remove('d-none');

        try {
            const response = await fetch('/api/user/password', {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({
                    currentPassword: currentPassword.value,
                    newPassword: newPassword.value
                })
            });

            if (response.ok) {
                showAlert('비밀번호가 성공적으로 변경되었습니다.', 'success');
                form.reset();
            } else {
                const errorData = await response.json();
                showAlert(errorData.message || '비밀번호 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('비밀번호 변경 오류:', error);
            showAlert('서버 오류가 발생했습니다. 다시 시도해주세요.');
        } finally {
            // 로딩 상태 해제
            changeButton.disabled = false;
            buttonText.classList.remove('d-none');
            spinner.classList.add('d-none');
        }
    });
});
