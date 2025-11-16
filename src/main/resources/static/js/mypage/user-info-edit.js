 // ===========================================
    // Mocking for Standalone Execution
    // 실제 환경에서는 외부 JS 파일에서 로드됨
    // ===========================================
    const CONFIG = {
        API: {
            ENDPOINTS: {
                USER_INFO: "/api/user/info"
            },
            MOCK_DATA: {
                name: "김철수",
                studentId: "20230001",
                major: "컴퓨터공학과",
                email: "kim.cs@purum.ac.kr",
                phone: "010-1234-5678"
            }
        },
        MESSAGES: {
            SUCCESS: {
                USER_UPDATE: "사용자 정보가 성공적으로 업데이트되었습니다."
            },
            CONFIRM: {
                CANCEL_EDIT: "수정된 내용이 있습니다. 정말 취소하시겠습니까? (저장되지 않은 변경 사항은 손실됩니다.)"
            }
        }
    };

    // Custom alert/error handler
    const ErrorHandler = {
        showError: (error) => {
            console.error("Error (Mock):", error);
            alert("오류 발생: " + (error.message || "알 수 없는 오류가 발생했습니다."));
        },
        showSuccess: (message) => {
            console.log("Success (Mock):", message);
            alert("성공: " + message);
        }
    };

    // Mock API utility
    const ApiUtils = {
        request: async (url, options) => {
            console.log(`Mock API Request to ${url}`, options);
            await new Promise(resolve => setTimeout(resolve, 500)); // Simulate network delay

            if (url === CONFIG.API.ENDPOINTS.USER_INFO) {
                if (options.method === 'PATCH') {
                    // Simulate successful update and update mock data
                    const data = JSON.parse(options.body);
                    CONFIG.API.MOCK_DATA.email = data.email;
                    CONFIG.API.MOCK_DATA.phone = data.phone;
                    return { success: true, message: "Update successful" };
                }
                // Simulate GET request (for loadUserInfo)
                return CONFIG.API.MOCK_DATA;
            }
            throw new Error("Invalid Mock API Endpoint");
        }
    };

    // ===========================================
    // user-info-edit.js 내용 시작
    // ===========================================
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('userInfoForm');
        const updateBtn = document.getElementById('updateBtn');
        const cancelBtn = document.getElementById('cancelBtn');

        // Custom Modal elements (Bootstrap 5 JS Bundle이 로드되어 있어야 함)
        const confirmModalElement = document.getElementById('customConfirmModal');
        if (!confirmModalElement) {
            console.error("Error: Custom modal element not found.");
            return;
        }
        // Bootstrap 모달 인스턴스 생성
        const confirmModal = new bootstrap.Modal(confirmModalElement);
        const confirmModalBody = document.getElementById('customConfirmModalBody');
        const confirmYesBtn = document.getElementById('customConfirmYesBtn');

        let originalData = {};

        /**
         * 초기 사용자 정보를 로드하고 폼에 채우는 함수
         */
        async function loadUserInfo() {
            try {
                const data = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, { method: 'GET' });

                document.getElementById('name').value = data.name || '';
                document.getElementById('studentId').value = data.studentId || '';
                document.getElementById('major').value = data.major || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phone').value = data.phone || '';

                // 초기 데이터를 저장하여 변경 여부를 확인하는 데 사용
                originalData = {
                    email: data.email,
                    phone: data.phone
                };

            } catch (error) {
                if (typeof ErrorHandler !== 'undefined') {
                    ErrorHandler.showError(error);
                } else {
                    console.error("사용자 정보 로드 오류:", error);
                }
            }
        }

        /**
         * 현재 폼 데이터가 초기 데이터와 다른지 확인하는 함수
         * @returns {boolean} 데이터가 변경되었으면 true
         */
        function isDataChanged() {
            const currentData = {
                email: document.getElementById('email').value.trim(),
                phone: document.getElementById('phone').value.trim()
            };

            return currentData.email !== originalData.email || currentData.phone !== originalData.phone;
        }

        /**
         * 폼 유효성 검사 함수
         * @returns {boolean} 유효성 검사 통과 여부
         */
        function validateForm() {
            clearErrors();
            let isValid = true;

            const emailInput = document.getElementById('email');
            const phoneInput = document.getElementById('phone');

            // 1. 이메일 유효성 검사 (간단한 형식 체크)
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(emailInput.value.trim())) {
                document.getElementById('email-error').textContent = '유효한 이메일 주소를 입력해주세요.';
                isValid = false;
            }

            // 2. 전화번호 유효성 검사 (XX-XXX(X)-XXXX 형식)
            const phoneRegex = /^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$/;
            if (!phoneRegex.test(phoneInput.value.trim())) {
                document.getElementById('phone-error').textContent = '유효한 휴대폰 번호(010-XXXX-XXXX)를 입력해주세요.';
                isValid = false;
            }

            return isValid;
        }

        /**
         * 사용자 정보를 업데이트하는 함수
         */
        async function updateUserInfo() {
            if (!validateForm()) {
                return;
            }

            if (!isDataChanged()) {
                if (typeof ErrorHandler !== 'undefined') {
                    ErrorHandler.showSuccess("변경 사항이 없습니다.");
                } else {
                    console.log("변경 사항 없음");
                }
                return;
            }

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
                loadUserInfo(); // 성공 후 데이터 다시 로드
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


        // 폼 로드 (초기 데이터 불러오기)
        loadUserInfo();

        // 폼 제출 이벤트 리스너
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            updateUserInfo();
        });

        // '취소' 버튼 클릭 시 Custom Modal 호출
        cancelBtn.addEventListener('click', function() {
            if (isDataChanged()) {
                // 변경 사항이 있을 경우 확인 모달 팝업
                confirmModalBody.textContent = CONFIG.MESSAGES.CONFIRM.CANCEL_EDIT;
                confirmYesBtn.onclick = () => {
                    confirmModal.hide();
                    loadUserInfo(); // 변경 사항 취소 (초기 데이터 재로드)
                    clearErrors();
                };
                confirmModal.show();
            } else {
                // 변경 사항이 없으면 바로 취소 처리 (대시보드로 이동한다고 가정)
                console.log("변경 사항 없음. 취소 처리.");
                window.location.href = "/mypage/dashboard"; // Mock navigation
            }
        });

    });