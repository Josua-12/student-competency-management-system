document.addEventListener('DOMContentLoaded', () => {

    // 1. DOM 요소 및 변수
    const sectionListBody = document.getElementById('sectionListBody');
    const sectionModalEl = document.getElementById('sectionModal');
    const sectionModal = new bootstrap.Modal(sectionModalEl);
    const sectionForm = document.getElementById('sectionForm');
    const btnSave = document.getElementById('btnSaveSection');
    const modalTitle = document.getElementById('sectionModalLabel');

    // JWT 토큰 가져오기 (예: localStorage에 'accessToken'이라는 키로 저장된 경우)
    function getJwtToken() {
        return localStorage.getItem('accessToken'); // 저장 위치에 따라 수정 필요
    }


    // fetch 헤더를 생성하는 헬퍼 함수
    function getFetchHeaders(includeContentType = true) {
        const headers = {};
        if (includeContentType) {
            headers['Content-Type'] = 'application/json';
        }
        // JWT 토큰이 있으면 Authorization 헤더에 추가
        const token = getJwtToken()
        if (token) {
            headers['Authorization'] = 'Bearer' + token;
        }
        return headers;
    }
    // ==========================================================

    // 2. 초기 로딩: 목록 가져오기
    loadSections();

    // 3. 이벤트 리스너

    // [모달 열기] 등록 버튼
    document.getElementById('btnOpenCreateModal').addEventListener('click', () => {
        openModal(); // Create Mode
    });

    // [저장] 모달 내 저장 버튼
    btnSave.addEventListener('click', saveSection);

    // [수정/삭제] 테이블 내 버튼 (이벤트 위임)
    sectionListBody.addEventListener('click', (e) => {
        const target = e.target;
        const btn = target.closest('button');
        if (!btn) return;

        const id = btn.dataset.id;

        if (btn.classList.contains('btn-edit')) {
            // 수정 모드
            fetchSectionDetail(id);
        } else if (btn.classList.contains('btn-delete')) {
            // 삭제
            deleteSection(id);
        }
    });


    /* ================= Functions ================= */

    /**
     * API: 진단 목록 조회 및 렌더링 (GET 요청)
     */
    function loadSections() {
        // [수정됨] GET 요청은 CSRF가 필요 없으므로 헤더를 안 보냅니다.
        fetch('/competency-admin/assessment-section/api/sections')
            .then(res => {
                if (!res.ok) throw new Error('목록 로딩 실패');
                return res.json();
            })
            .then(data => {
                renderTable(data);
            })
            .catch(err => {
                console.error(err);
                sectionListBody.innerHTML = `<tr><td colspan="6" class="text-danger">데이터를 불러오지 못했습니다. (오류: ${err.message})</td></tr>`;
            });
    }

    /**
     * 테이블 렌더링
     */
    function renderTable(sections) {
        if (!sections || sections.length === 0) {
            sectionListBody.innerHTML = `<tr><td colspan="6" class="text-center py-4">등록된 진단 회차가 없습니다.</td></tr>`;
            return;
        }

        sectionListBody.innerHTML = sections.map(section => {
            const start = formatDate(section.startDate);
            const end = formatDate(section.endDate);
            // [수정됨] DTO 필드명을 isActive -> active 로 변경 (DTO 확인 필요)
            const isActive = section.active;

            const now = new Date();
            const startDate = new Date(section.startDate);
            const endDate = new Date(section.endDate);

            let statusBadge = '';
            if (!isActive) {
                statusBadge = '<span class="badge badge-status-inactive">비활성</span>';
            } else if (now > endDate) {
                statusBadge = '<span class="badge badge-status-expired">종료됨</span>';
            } else if (now < startDate) {
                // [수정됨] '예정됨' 뱃지 스타일 추가
                statusBadge = '<span class="badge bg-info text-dark">예정됨</span>';
            } else {
                statusBadge = '<span class="badge badge-status-active">진행 중</span>';
            }

            return `
                <tr>
                    <td>${section.id}</td>
                    <td class="text-start fw-bold">${section.title}</td>
                    <td class="small">${start} ~ ${end}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <div class="form-check form-switch d-flex justify-content-center">
                            <input class="form-check-input" type="checkbox" disabled ${isActive ? 'checked' : ''}>
                        </div>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-outline-secondary btn-edit me-1" data-id="${section.id}">
                            <i class="fas fa-edit"></i> 수정
                        </button>
                        <button class="btn btn-sm btn-outline-danger btn-delete" data-id="${section.id}">
                            <i class="fas fa-trash"></i> 삭제
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }

    /**
     * API: 상세 조회 (수정용)
     */
    function fetchSectionDetail(id) {
        fetch(`/competency-admin/assessment-section/api/sections/${id}`)
            .then(res => res.json())
            .then(data => {
                openModal(data); // Edit Mode
            })
            .catch(err => alert('상세 정보 로딩 실패: ' + err));
    }

    /**
     * API: 저장 및 수정 (POST 요청)
     */
    function saveSection() {
        if (!sectionForm.checkValidity()) {
            sectionForm.reportValidity();
            return;
        }

        btnSave.disabled = true;
        const originalBtnText = btnSave.innerHTML;
        btnSave.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 저장 중...';

        const formData = {
            id: document.getElementById('sectionId').value || null,
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            startDate: document.getElementById('startDate').value,
            endDate: document.getElementById('endDate').value,
            // [수정됨] DTO 필드명 확인 (active vs isActive)
            active: document.getElementById('isActive').checked
        };

        fetch('/competency-admin/assessment-section/api/sections', {
            method: 'POST',
            // [수정됨] 헬퍼 함수로 헤더 생성
            headers: getFetchHeaders(),
            body: JSON.stringify(formData)
        })
            .then(res => res.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert(data.message || '저장되었습니다.');
                sectionModal.hide();
                loadSections(); // 목록 새로고침
            })
            .catch(err => alert('저장 실패: ' + err.message))
            .finally(() => {
                btnSave.disabled = false;
                btnSave.innerHTML = originalBtnText;
            });
    }

    /**
     * API: 삭제 (DELETE 요청)
     */
    function deleteSection(id) {
        if(!confirm('정말 이 진단 회차를 삭제하시겠습니까?\n(이미 진행된 진단 결과가 있다면 문제가 될 수 있습니다.)')) return;

        fetch(`/competency-admin/assessment-section/api/sections/${id}`, {
            method: 'DELETE',
            // [수정됨] 헬퍼 함수로 헤더 생성 (Content-Type 제외)
            headers: getFetchHeaders(false)
        })
            .then(res => res.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert('삭제되었습니다.');
                loadSections();
            })
            .catch(err => alert('삭제 실패: ' + err.message));
    }

    /**
     * Helper: 모달 열기
     */
    function openModal(data = null) {
        sectionForm.reset();

        if (data) {
            modalTitle.textContent = '진단 정보 수정';
            document.getElementById('sectionId').value = data.id;
            document.getElementById('title').value = data.title;
            document.getElementById('description').value = data.description;
            document.getElementById('startDate').value = data.startDate;
            document.getElementById('endDate').value = data.endDate;
            // [수정됨] DTO 필드명 확인 (active vs isActive)
            document.getElementById('isActive').checked = data.active;
        } else {
            modalTitle.textContent = '새 진단 등록';
            document.getElementById('sectionId').value = '';
            document.getElementById('isActive').checked = true;
        }

        sectionModal.show();
    }

    /**
     * Helper: 날짜 포맷 (YYYY-MM-DD HH:mm)
     */
    function formatDate(isoString) {
        if (!isoString) return '-';
        const date = new Date(isoString);
        return date.toLocaleString('ko-KR', {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', hour12: false
        });
    }
});