// DOM이 완전히 로드된 후 스크립트 실행
document.addEventListener("DOMContentLoaded", () => {

    // --- 1. 모바일 햄버거 메뉴 토글 기능 ---
    const mobileMenuBtn = document.getElementById("mobile-menu-btn");
    const mobileMenu = document.getElementById("mobile-menu");

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener("click", () => {
            // 햄버거 버튼 클릭 시 모바일 메뉴를 보여주거나 숨김
            const isExpanded = mobileMenuBtn.getAttribute("aria-expanded") === "true";
            mobileMenuBtn.setAttribute("aria-expanded", !isExpanded);

            if (mobileMenu.style.display === "block") {
                mobileMenu.style.display = "none";
            } else {
                mobileMenu.style.display = "block";
            }
        });
    }

    // --- 2. Custom Modal (alert 대체) 기능 ---
    const modal = document.getElementById("custom-modal");
    const modalMessage = document.getElementById("modal-message");
    const modalCloseBtn = document.getElementById("modal-close-btn");

    // 모달창 닫기 버튼 이벤트
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener("click", () => {
            modal.style.display = "none";
        });
    }

    // 모달창 표시 함수
    function showModal(message) {
        if (modal && modalMessage) {
            modalMessage.textContent = message;
            modal.style.display = "flex";
        } else {
            // 모달이 없으면 console.log로 대체
            console.log("Modal message:", message);
        }
    }

    // --- 3. 테이블 내 버튼 클릭 이벤트 (모달 사용) ---
    const tableBody = document.getElementById("applicationTableBody");

    if (tableBody) {
        tableBody.addEventListener("click", (event) => {
            // 클릭된 요소가 'action-btn' 클래스를 가졌는지 확인
            if (event.target.classList.contains("action-btn")) {
                const button = event.target;
                const action = button.dataset.action; // data-action 값 (cancel, register)

                // 버튼이 속한 행(tr)을 찾음
                const row = button.closest("tr");
                const programName = row.dataset.program;

                if (action === "cancel") {
                    // '취소' 버튼 클릭 시
                    showModal(`'${programName}' 프로그램의 신청을 취소하시겠습니까? (실제 취소 기능은 백엔드 구현이 필요합니다.)`);
                    // 여기에 실제 취소 로직 (e.g., fetch API 호출) 추가 가능
                } else if (action === "register") {
                    // '산출물 등록' 버튼 클릭 시
                    showModal(`'${programName}' 프로그램의 산출물을 등록합니다. (등록 페이지로 이동 또는 파일 업로드 로직)`);
                    // 여기에 실제 산출물 등록 로직 (e.g., 페이지 이동) 추가 가능
                }
            }
        });
    }
});