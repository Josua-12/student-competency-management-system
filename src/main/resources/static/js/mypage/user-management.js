// API 엔드포인트
const USER_INFO_API = '/api/user-info';
const PASSWORD_CHANGE_API = '/api/user/password';

/**
 * 메시지 알림 UI를 표시합니다.
 * @param {string} message - 표시할 메시지
 * @param {string} type - 'success', 'danger', 'warning' 중 하나 (Bootstrap alert class suffix)
 * @param {string} elementId - 메시지를 표시할 HTML 요소의 ID
 */
function displayMessage(message, type, elementId = 'alertMessage') {
    const alertElement = document.getElementById(elementId);
    if (!alertElement) return;

    alertElement.className = `alert alert-${type}`;
    alertElement.textContent = message;
    alertElement.classList.remove('d-none');

    // 5초 후 메시지 자동 숨김 (성공 메시지의 경우)
    if (type === 'success') {
        setTimeout(() => {
            alertElement.classList.add('d-none');
        }, 5000);
    }
}

/**
 * 버튼 상태 (로딩 스피너)를 토글합니다.
 * @param {string} buttonId - 버튼 요소의 ID
 * @param {boolean} isLoading - 로딩 상태 여부
 * @param {string} originalText - 로딩이 끝났을 때 표시할 원래 텍스트
 */
function toggleButtonLoading(buttonId, isLoading, originalText) {
    const button = document.getElementById(buttonId);
    const spinner = button.querySelector('#spinner');
    const textSpan = button.querySelector('#buttonText');

    if (isLoading) {
        button.disabled = true;
        textSpan.textContent = '처리 중...';
        spinner.classList.remove('d-none');
    } else {
        button.disabled = false;
        textSpan.textContent = originalText;
        spinner.classList.add('d-none');
    }
}

/**
 * 1. [회원 정보 수정 페이지] 초기 사용자 정보를 서버에서 불러와 폼에 채웁니다. (GET /api/user/info)
 */
async function fetchUserInfo() {
    try {
        const response = await fetch(USER_INFO_API, { method: 'GET' });

        if (!response.ok) {
            throw new Error('사용자 정보를 불러오는 데 실패했습니다.');
        }

        const data = await response.json();

        // DTO에 맞춰 폼 필드 업데이트
        document.getElementById('studentId').value = data.userNum || '';
        document.getElementById('name').value = data.name || '';
        document.getElementById('birthDate').value = data.birthDate || '';
        document.getElementById('email').value = data.email || '';
        document.getElementById('phone').value = data.phone || '';

    } catch (error) {
        console.error('Fetch Error:', error);
        displayMessage('사용자 정보를 불러올 수 없습니다. 다시 시도해 주세요.', 'danger');
    }
}

/**
 * 2. [회원 정보 수정 페이지] 이메일과 주소 정보를 서버로 전송하여 수정합니다. (PATCH /api/user/info)
 */
async function updateUserInfo() {
    const buttonId = 'updateButton';
    toggleButtonLoading(buttonId, true, '정보 수정');

    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();

    // 간단한 클라이언트 측 유효성 검사
    if (!email) {
        displayMessage('이메일은 필수 입력 항목입니다.', 'danger');
        toggleButtonLoading(buttonId, false, '정보 수정');
        return;
    }

    const requestBody = { email, phone };

    try {
        const response = await fetch(USER_INFO_API, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            // 성공 시 메시지 표시
            const result = await response.json();

            displayMessage('정보가 성공적으로 수정되었습니다.', 'success');
            // 폼에서 is-invalid 클래스 제거 (Bootstrap 유효성 피드백 초기화)
            document.getElementById('userInfoForm').classList.remove('was-validated');

        } else if (response.status === 400) {
            // DTO 유효성 검사 실패 (Bad Request)
            const errorData = await response.json();
            // 백엔드에서 에러 메시지가 잘 온다면 이를 사용
            const errorMessage = errorData.message || '입력하신 정보가 유효하지 않습니다.';
            displayMessage(errorMessage, 'danger');
        } else {
            throw new Error(`서버 오류: ${response.status}`);
        }

    } catch (error) {
        console.error('Update Error:', error);
        displayMessage('정보 수정 중 오류가 발생했습니다. 다시 시도해 주세요.', 'danger');
    } finally {
        toggleButtonLoading(buttonId, false, '정보 수정');
    }
}


/**
 * 3. [비밀번호 변경 페이지] 현재/새 비밀번호를 서버로 전송하여 변경합니다. (PATCH /api/user/password)
 */
async function changePassword() {
    const buttonId = 'changeButton';
    toggleButtonLoading(buttonId, true, '비밀번호 변경');

    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const alertElement = document.getElementById('alertMessage');
    alertElement.classList.add('d-none'); // 기존 메시지 숨기기

    // 클라이언트 측 비밀번호 일치 확인
    if (newPassword !== confirmPassword) {
        document.getElementById('confirmPassword').classList.add('is-invalid');
        displayMessage('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.', 'danger');
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
        return;
    }

    // 새 비밀번호 유효성 검사 (패턴 매칭)
    const passwordPattern = new RegExp(document.getElementById('newPassword').pattern);
    if (!passwordPattern.test(newPassword)) {
        document.getElementById('newPassword').classList.add('is-invalid');
        displayMessage('비밀번호는 8자 이상, 대문자, 소문자, 숫자를 포함해야 합니다.', 'danger');
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
        return;
    }

    // 유효성 통과 시, 모든 invalid 클래스 제거
    document.getElementById('newPassword').classList.remove('is-invalid');
    document.getElementById('confirmPassword').classList.remove('is-invalid');


    const requestBody = {
        currentPassword: currentPassword,
        newPassword: newPassword
    };

    try {
        const response = await fetch(PASSWORD_CHANGE_API, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        if (response.ok) {
            displayMessage('비밀번호가 성공적으로 변경되었습니다.', 'success');
            // 폼 초기화
            document.getElementById('passwordChangeForm').reset();

        } else if (response.status === 400) {
            // DTO 유효성 또는 현재 비밀번호 불일치 오류
            const errorText = await response.text(); // JSON이 아닐 수도 있으므로 텍스트로 받음

            let errorMessage = '비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요.';

            // 백엔드에서 제공하는 에러 메시지가 있다면 사용 (예: "현재 비밀번호가 일치하지 않습니다.")
            try {
                const errorData = JSON.parse(errorText);
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
                // JSON 파싱 실패 시 기본 메시지 사용
            }

            displayMessage(errorMessage, 'danger');

        } else {
            throw new Error(`서버 오류: ${response.status}`);
        }

    } catch (error) {
        console.error('Password Change Error:', error);
        displayMessage('비밀번호 변경 중 오류가 발생했습니다. 다시 시도해 주세요.', 'danger');
    } finally {
        toggleButtonLoading(buttonId, false, '비밀번호 변경');
    }
}