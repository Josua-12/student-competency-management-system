 // alert() 대신 사용할 커스텀 모달 기능
    const customModal = document.getElementById('custom-modal');
    const modalMessage = document.getElementById('modal-message');
    const modalCloseBtn = document.getElementById('modal-close-btn'); // 확인 버튼 요소 재정의

    /**
     * 커스텀 모달을 표시하는 함수
     * @param {string} message - 모달에 표시할 메시지
     */
    function showCustomModal(message) {
        modalMessage.textContent = message;
        customModal.style.display = 'flex';
    }

    // 모달 닫기 버튼 이벤트 리스너: 확인 버튼 클릭 시 닫기
    modalCloseBtn.addEventListener('click', () => {
        customModal.style.display = 'none';
    });

    // 이전에 있던 배경 클릭으로 닫는 로직은 제거됨.

    // 테이블 상호작용 로직
    document.addEventListener('DOMContentLoaded', () => {
        const tableBody = document.getElementById('applicationTableBody');

        // 테이블 본문 클릭 이벤트 리스너
        tableBody.addEventListener('click', (event) => {
            const target = event.target;

            // 클릭된 요소가 'action-btn' 클래스를 가지는지 확인
            if (target.classList.contains('action-btn')) {
                const action = target.getAttribute('data-action');
                const row = target.closest('tr');
                const programName = row.getAttribute('data-program');
                const status = row.getAttribute('data-status');

                // '취소' 버튼 클릭 시 로직
                if (action === 'cancel') {
                    if (status === '신청 완료') {
                        // 메시지 수정: 닫으려면 확인 버튼을 클릭하도록 안내
                        showCustomModal(`'${programName}' 프로그램 신청을 정말로 취소하시겠습니까? (기능 구현 필요). 닫으려면 확인 버튼을 클릭하세요.`);
                    } else {
                        // 메시지 수정: 닫으려면 확인 버튼을 클릭하도록 안내
                        showCustomModal(`'${programName}' 프로그램은 현재 '${status}' 상태이므로 취소할 수 없습니다. 닫으려면 확인 버튼을 클릭하세요.`);
                    }
                }
                // '산출물 등록' 버튼 클릭 시 로직
                else if (action === 'register') {
                    if (status === '이수 완료') {
                        // 메시지 수정: 닫으려면 확인 버튼을 클릭하도록 안내
                        showCustomModal(`'${programName}' 프로그램의 산출물 등록 페이지로 이동합니다. (기능 구현 필요). 닫으려면 확인 버튼을 클릭하세요.`);
                    } else {
                        // 메시지 수정: 닫으려면 확인 버튼을 클릭하도록 안내
                        showCustomModal(`'${programName}' 프로그램은 아직 이수 완료 상태가 아닙니다. 산출물 등록이 불가능합니다. 닫으려면 확인 버튼을 클릭하세요.`);
                    }
                }
            }
        });
    });