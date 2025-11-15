// static/js/mypage/user-info-edit.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('userInfoForm');
    const updateBtn = document.getElementById('updateBtn');
    const cancelBtn = document.getElementById('cancelBtn');

    // Custom Modal elements
    const confirmModal = new bootstrap.Modal(document.getElementById('customConfirmModal'));
    const confirmModalBody = document.getElementById('customConfirmModalBody');
    const confirmYesBtn = document.getElementById('customConfirmYesBtn');

    let originalData = {};

    // 폼 로드 (초기 데이터 불러오기)
    loadUserInfo();

    // 폼 제출 이벤트 리스너
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        updateUserInfo();
    });

    // '취소' 버튼 클릭 시 Custom Modal 호출
    cancelBtn.addEventListener('click', function() {
        // CONFIG가 외부에서 로드된다고 가정하고 메시지 사용
        const message = typeof CONFIG !== 'undefined' && CONFIG.MESSAGES && CONFIG.MESSAGES.SUCCESS.CANCEL_CONFIRM
            ? CONFIG.MESSAGES.SUCCESS.CANCEL_CONFIRM
            : "수정사항을 취소하고 원래 정보로 되돌리시겠습니까?";

        // 메시지 설정 및 모달 표시
        confirmModalBody.textContent = message;
        confirmModal.show();

        // '예' 버튼 리스너 설정 (이전 리스너 제거 후 새로 설정)
        confirmYesBtn.onclick = function() {
            confirmModal.hide(); // 모달 숨기기
            loadUserInfo();      // 정보 다시 로드 (취소)
            // 성공 또는 취소 알림이 필요하다면 여기에 추가
            if (typeof ErrorHandler !== 'undefined') {
                ErrorHandler.showInfo("수정이 취소되었습니다.");
            } else {
                console.log("수정이 취소되었습니다.");
            }
        };
    });

    /**
     * 사용자 정보를 서버에서 불러와 폼에 채우는 함수
     */
    async function loadUserInfo() {
        try {
            // 외부 JS 파일에 정의된 ApiUtils를 사용한다고 가정
            const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, { method: 'GET' });
            const data = await response.json();
            originalData = data;

            // 폼 필드 업데이트
            document.getElementById('name').value = data.name || '';
            document.getElementById('email').value = data.email || '';
            document.getElementById('phone').value = data.phone || '';
            document.getElementById('department').value = data.department || '';
            document.getElementById('grade').value = data.grade || '';
        } catch (error) {
            // 외부 JS 파일에 정의된 ErrorHandler를 사용한다고 가정
            if (typeof ErrorHandler !== 'undefined') {
                 ErrorHandler.showError(error);
            } else {
                console.error("사용자 정보 로드 오류:", error);
            }
        }
    }

    /**
     * 수정된 사용자 정보를 서버에 전송하는 함수
     */
    async function updateUserInfo() {
        clearErrors();

        const formData = {
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value
        };

        try {
            // 외부 JS 파일에 정의된 ApiUtils를 사용한다고 가정
            const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, {
                method: 'PATCH',
                body: JSON.stringify(formData)
            });

            // 성공 메시지 표시 및 데이터 다시 로드
            if (typeof ErrorHandler !== 'undefined') {
                ErrorHandler.showSuccess(CONFIG.MESSAGES.SUCCESS.USER_UPDATE);
            } else {
                console.log("정보가 성공적으로 업데이트되었습니다.");
            }
            loadUserInfo();
        } catch (error) {
            // 외부 JS 파일에 정의된 ErrorHandler를 사용한다고 가정
            if (typeof ErrorHandler !== 'undefined') {
                ErrorHandler.showError(error);
            } else {
                console.error("사용자 정보 업데이트 오류:", error);
            }
        }
    }

    /**
     * 폼의 에러 메시지를 지우는 함수
     */
    function clearErrors() {
        document.querySelectorAll('.error-message').forEach(element => {
            element.textContent = '';
        });
    }
});