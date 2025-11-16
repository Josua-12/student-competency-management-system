// static/js/mypage/user-management.js
async function fetchUserInfo() {
    try {
        const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO);
        const data = await response.json();

        document.getElementById('studentId').value = data.userNum || '';
        document.getElementById('name').value = data.name || '';
        document.getElementById('birthDate').value = data.birthDate || '';
        document.getElementById('email').value = data.email || '';
        document.getElementById('phone').value = data.phone || '';

    } catch (error) {
        ErrorHandler.showError(error);
    }
}

async function updateUserInfo() {
    const buttonId = 'updateButton';
    toggleButtonLoading(buttonId, true, '정보 수정');

    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();

    if (!email) {
        ErrorHandler.showError(new Error(CONFIG.MESSAGES.ERROR.EMAIL_REQUIRED));
        toggleButtonLoading(buttonId, false, '정보 수정');
        return;
    }

    const requestBody = { email, phone };

    try {
        await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, {
            method: 'PATCH',
            body: JSON.stringify(requestBody)
        });

        ErrorHandler.showSuccess(CONFIG.MESSAGES.SUCCESS.USER_UPDATE);
        document.getElementById('userInfoForm').classList.remove('was-validated');

    } catch (error) {
        ErrorHandler.showError(error);
    } finally {
        toggleButtonLoading(buttonId, false, '정보 수정');
    }
}

async function changePassword() {
    const buttonId = 'changeButton';
    toggleButtonLoading(buttonId, true, '비밀번호 변경');

    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    document.getElementById('alertMessage').classList.add('d-none');

    if (newPassword !== confirmPassword) {
        document.getElementById('confirmPassword').classList.add('is-invalid');
        ErrorHandler.showError(new Error(CONFIG.MESSAGES.ERROR.PASSWORD_MISMATCH));
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
        return;
    }

    if (!CONFIG.VALIDATION.PASSWORD_PATTERN.test(newPassword)) {
        document.getElementById('newPassword').classList.add('is-invalid');
        ErrorHandler.showError(new Error(CONFIG.MESSAGES.ERROR.PASSWORD_PATTERN));
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
        return;
    }

    document.getElementById('newPassword').classList.remove('is-invalid');
    document.getElementById('confirmPassword').classList.remove('is-invalid');

    const requestBody = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    try {
        await ApiUtils.request(CONFIG.API.ENDPOINTS.PASSWORD_CHANGE, {
            method: 'PATCH',
            body: JSON.stringify(requestBody)
        });

        ErrorHandler.showSuccess(CONFIG.MESSAGES.SUCCESS.PASSWORD_CHANGE);
        document.getElementById('passwordChangeForm').reset();

    } catch (error) {
        ErrorHandler.showError(error);
    } finally {
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
    }
}

function toggleButtonLoading(buttonId, isLoading, originalText) {
    const button = document.getElementById(buttonId);
    const spinner = button.querySelector('#spinner');
    const textSpan = button.querySelector('#buttonText');

    if (isLoading) {
        button.disabled = true;
        textSpan.textContent = CONFIG.UI.LOADING_TEXT;
        spinner.classList.remove('d-none');
    } else {
        button.disabled = false;
        textSpan.textContent = originalText;
        spinner.classList.add('d-none');
    }
}

function initPasswordValidation() {
    const form = document.getElementById('passwordChangeForm');
    if (!form) return;

    form.addEventListener('input', () => {
        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword');

        if (newPass !== confirmPass.value && confirmPass.value.length > 0) {
            confirmPass.classList.add('is-invalid');
            document.getElementById('confirmPasswordFeedback').innerText = '새 비밀번호와 일치하지 않습니다.';
        } else {
            confirmPass.classList.remove('is-invalid');
            confirmPass.setCustomValidity('');
        }
    });
}

function initRealTimeValidation() {
    // 이메일 실시간 검증
    FormValidator.setupRealTimeValidation(
        'email',
        FormValidator.validateEmail,
        '올바른 이메일 형식을 입력해주세요.'
    );

    // 전화번호 실시간 검증
    FormValidator.setupRealTimeValidation(
        'phone',
        FormValidator.validatePhone,
        '올바른 전화번호 형식을 입력해주세요. (예: 010-1234-5678)'
    );

    // 새 비밀번호 실시간 검증
    FormValidator.setupRealTimeValidation(
        'newPassword',
        FormValidator.validatePassword,
        '8자 이상, 대문자, 소문자, 숫자를 포함해야 합니다.'
    );
}

function initPasswordValidation() {
    const form = document.getElementById('passwordChangeForm');
    if (!form) return;

    const debouncedPasswordMatch = FormValidator.debounce(() => {
        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword');

        if (confirmPass.value.length === 0) {
            FormValidator.clearFieldError('confirmPassword');
            return;
        }

        if (newPass === confirmPass.value) {
            FormValidator.clearFieldError('confirmPassword');
        } else {
            FormValidator.showFieldError('confirmPassword', '새 비밀번호와 일치하지 않습니다.');
        }
    });

    form.addEventListener('input', debouncedPasswordMatch);
}

document.addEventListener('DOMContentLoaded', function() {
    ErrorHandler.init();
    initPasswordValidation();
    initRealTimeValidation();

    const passwordForm = document.getElementById('passwordChangeForm');
    if (passwordForm) {
        passwordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            changePassword();
        });
    }
});