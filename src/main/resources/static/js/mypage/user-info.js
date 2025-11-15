<!-- 통합 JavaScript (user-info.js 내용) -->
    // static/js/mypage/user-info.js

    // 외부에서 로드된다고 가정하는 메시지 및 API 설정 (Canvas 환경을 위해 임시로 정의)
    const CONFIG = {
        API: {
            ENDPOINTS: {
                USER_INFO: '/api/user/info' // 임시 API 경로
            }
        },
        MESSAGES: {
            SUCCESS: {
                USER_UPDATE: '정보가 성공적으로 수정되었습니다.',
                CANCEL_CONFIRM: '수정사항을 취소하고 되돌리시겠습니까?'
            },
            ERROR: {
                FETCH_FAILED: '사용자 정보를 불러오는데 실패했습니다.',
                UPDATE_FAILED: '정보 수정에 실패했습니다.'
            }
        }
    };

    // 모달 및 메시지 처리를 위한 유틸리티 함수 (user-info.js에서 사용되므로 인라인 정의)
    const ErrorHandler = {
        showSuccess: (message) => {
            const area = document.getElementById('messageArea');
            if (area) {
                area.className = 'alert alert-success d-block';
                area.textContent = message;
                setTimeout(() => area.classList.add('d-none'), 5000);
            }
        },
        showError: (error) => {
            const area = document.getElementById('messageArea');
            if (area) {
                area.className = 'alert alert-danger d-block';
                area.textContent = error.message || CONFIG.MESSAGES.ERROR.UPDATE_FAILED;
            }
        }
    };

    // ApiUtils는 실제 서버와의 통신을 시뮬레이션합니다.
    const ApiUtils = {
        // 실제 fetch 요청을 시뮬레이션
        request: async (url, options) => {
            console.log(`Simulating API call to ${url} with method ${options.method}`);

            // 시뮬레이션: PATCH 요청 성공 시 200 응답 반환
            if (url === CONFIG.API.ENDPOINTS.USER_INFO && options.method === 'PATCH') {
                await new Promise(resolve => setTimeout(resolve, 500)); // 딜레이 시뮬레이션
                const data = JSON.parse(options.body);
                // 유효성 검사 시뮬레이션
                if (!data.email || !data.phone) {
                     throw new Error("이메일과 연락처는 필수 항목입니다.");
                }
                return { status: 200, json: async () => ({}) };
            }

            // 시뮬레이션: GET 요청에 대한 응답
            if (url === CONFIG.API.ENDPOINTS.USER_INFO && options.method === 'GET') {
                await new Promise(resolve => setTimeout(resolve, 300));
                return {
                    status: 200,
                    json: async () => ({
                        name: '김푸름',
                        email: 'kim.pr@example.ac.kr',
                        phone: '010-1234-5678',
                        department: '컴퓨터공학과',
                        grade: '3학년'
                    })
                };
            }

            throw new Error('API 요청 시뮬레이션 오류');
        }
    };


    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('userInfoForm');
        const updateBtn = document.getElementById('updateBtn');
        const cancelBtn = document.getElementById('cancelBtn');

        // Custom Modal elements (Bootstrap Modal 객체 생성)
        const customConfirmModalEl = document.getElementById('customConfirmModal');
        const confirmModal = customConfirmModalEl ? new bootstrap.Modal(customConfirmModalEl) : null;
        const confirmModalBody = document.getElementById('customConfirmModalBody');
        const confirmYesBtn = document.getElementById('customConfirmYesBtn');

        let originalData = {};

        // 폼 로드 함수
        loadUserInfo();

        // 사용자 정보 로드 및 폼에 채우기
        async function loadUserInfo() {
            try {
                // 외부 JS 파일에 정의된 ApiUtils를 사용한다고 가정
                const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, { method: 'GET' });
                const data = await response.json();
                originalData = data; // 원본 데이터 저장

                document.getElementById('name').value = data.name || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phone').value = data.phone || '';
                document.getElementById('department').value = data.department || '';
                document.getElementById('grade').value = data.grade || '';

            } catch (error) {
                ErrorHandler.showError(error);
            }
        }

        // 사용자 정보 업데이트 함수
        async function updateUserInfo() {
            clearErrors();

            const email = document.getElementById('email').value;
            const phone = document.getElementById('phone').value;

            // 기본 유효성 검사
            if (!email || !phone) {
                 ErrorHandler.showError(new Error("이메일과 연락처는 필수 항목입니다."));
                 return;
            }

            const formData = { email, phone };

            try {
                // 외부 JS 파일에 정의된 ApiUtils를 사용한다고 가정
                const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, {
                    method: 'PATCH',
                    body: JSON.stringify(formData)
                });

                ErrorHandler.showSuccess(CONFIG.MESSAGES.SUCCESS.USER_UPDATE);
                loadUserInfo(); // 업데이트 후 정보 다시 로드 (원본 데이터 갱신)

            } catch (error) {
                ErrorHandler.showError(error);
            }
        }

        // 오류 메시지 초기화
        function clearErrors() {
            document.querySelectorAll('.error-message').forEach(element => {
                element.textContent = '';
            });
            const area = document.getElementById('messageArea');
            if (area) area.classList.add('d-none');
        }

        // '수정' 버튼 클릭 이벤트 (폼 제출)
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            updateUserInfo();
        });

        // '취소' 버튼 클릭 시 Custom Modal 호출
        cancelBtn.addEventListener('click', function() {
            if (confirmModal && confirmModalBody && confirmYesBtn) {
                // CONFIG가 외부에서 로드된다고 가정하고 메시지 사용
                const message = CONFIG.MESSAGES.SUCCESS.CANCEL_CONFIRM;

                // 메시지 설정 및 모달 표시
                confirmModalBody.textContent = message;
                confirmModal.show();

                // '예' 버튼 리스너 설정 (이전 리스너 제거 후 새로 설정)
                // Bootstrap 모달은 DOM에서 '예' 버튼을 재사용하므로, onclick을 사용하거나
                // 이벤트 리스너를 추가/제거하는 방식으로 동작을 덮어씁니다.
                confirmYesBtn.onclick = function() {
                    confirmModal.hide(); // 모달 숨기기
                    loadUserInfo();      // 정보 다시 로드 (원본 데이터로 되돌리기)
                    clearErrors();       // 메시지 초기화
                };
            }
        });

    });