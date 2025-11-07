document.addEventListener('DOMContentLoaded', () => {

    /* ==================================================================
    == 1. 역량 트리 및 상세 폼 (기존 로직)
    ================================================================== */

    // 1-1. DOM 요소 캐시
    const detailView = document.getElementById('detailView');
    const placeholder = document.getElementById('detailPlaceholder');
    const form = document.getElementById('competencyForm');
    const formTitle = document.getElementById('formTitle');
    const treeContainer = document.getElementById('competencyTree');
    const saveButton = document.getElementById('saveButton');
    const deleteButton = document.getElementById('deleteButton');

    // '문항 관리' 탭의 테이블 body 캐시
    const questionListBody = document.getElementById('questionListBody');

    // 1-2. CSRF 토큰
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    let tree;

    // 1-3. 페이지 로드 시 트리 데이터 가져오기 (fetch)
    fetch('/admin/competency/api/tree')
        .then(response => response.json())
        .then(treeData => {
            initializeTree(treeData);
        })
        .catch(error => {
            console.error(error);
            treeContainer.innerHTML = `<div class="alert alert-danger">트리 로딩 실패</div>`;
        });

    /**
     * 1-4. TUI-Tree 초기화 및 이벤트 바인딩
     */
    function initializeTree(treeData) {
        tree = new tui.Tree(treeContainer, {
            data: treeData,
            nodeDefaultState: 'opened',
            nodeIconClass: 'tui-tree-ico-file',
            nodeIconClassOpened: 'tui-tree-ico-opened',
            nodeIconClassClosed: 'tui-tree-ico-closed',
        });

        // 1-5. 트리 노드 선택(클릭) 이벤트
        tree.on('select', (event) => {
            const nodeId = event.nodeId;
            if (!nodeId) return;

            // (A) 역량 상세 정보 fetch (기존 로직)
            fetch(`/admin/competency/api/competencies/${nodeId}`)
                .then(response => response.json())
                .then(competencyDto => {
                    showDetailView(competencyDto, competencyDto.parentId || '', false);

                    // (B) 역량 상세 정보 로딩 성공 시,
                    //     이어서 '문항 목록'을 불러오는 함수 호출
                    loadQuestions(nodeId);
                })
                .catch(error => alert('상세 정보 로딩 실패: ' + error.message));
        });
    }

    // 1-6. '최상위 추가' 버튼
    document.getElementById('addNewRootCompetency').addEventListener('click', () => {
        showDetailView(null, null, false);
    });

    // 1-7. '하위 역량 추가' 버튼
    document.getElementById('addChildButton').addEventListener('click', () => {
        const selectedNodeId = tree.getSelectedNodeId();
        if (!selectedNodeId) {
            alert('하위 역량을 추가할 상위 역량을 왼쪽 트리에서 먼저 선택하세요.');
            return;
        }
        showDetailView(null, selectedNodeId, true);
    });

    // 1-8. '역량 저장' 버튼 (C/U)
    saveButton.addEventListener('click', (e) => {
        e.preventDefault();
        const formData = {
            id: document.getElementById('competencyId').value || null,
            parentId: document.getElementById('parentId').value || null,
            name: document.getElementById('compName').value,
            compCode: document.getElementById('compCode').value,
            description: document.getElementById('compDescription').value,
            displayOrder: parseInt(document.getElementById('compOrder').value, 10),
            isActive: document.getElementById('compActive').checked,
            adviceHigh: document.getElementById('compAdviceHigh').value,
            adviceLow: document.getElementById('compAdviceLow').value
        };

        fetch('/admin/competency/api/competencies', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert(data.message || '저장되었습니다.');
                window.location.reload();
            })
            .catch(error => alert('저장 실패: ' + error.message));
    });

    // 1-9. '역량 삭제' 버튼 (D)
    deleteButton.addEventListener('click', (e) => {
        e.preventDefault();
        const competencyId = document.getElementById('competencyId').value;
        const competencyName = document.getElementById('compName').value;

        if (!competencyId) return alert('삭제할 역량이 선택되지 않았습니다.');
        if (!confirm(`'${competencyName}' 역량을 정말 삭제하시겠습니까?`)) return;

        fetch(`/admin/competency/api/competencies/${competencyId}`, {
            method: 'DELETE',
            headers: { [csrfHeader]: csrfToken }
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert(data.message || '삭제되었습니다.');
                window.location.reload();
            })
            .catch(error => alert('삭제 실패: ' + error.message));
    });

    /**
     * 1-10. (Helper) 상세정보 뷰 표시 함수
     */
    function showDetailView(competency, parentId, isChild) {
        placeholder.style.display = 'none';
        detailView.style.display = 'block';
        form.reset();
        new bootstrap.Tab(document.getElementById('info-tab')).show();

        // 폼을 채우거나 비울 때, 문항 목록도 비움
        questionListBody.innerHTML = '<tr><td colspan="6" class="text-center py-4">역량을 선택하세요.</td></tr>';

        if (competency) {
            // (A) 기존 역량 수정
            formTitle.textContent = '역량 정보 수정';
            document.getElementById('competencyId').value = competency.id;
            document.getElementById('parentId').value = competency.parentId || '';
            document.getElementById('compName').value = competency.name;
            document.getElementById('compCode').value = competency.compCode;
            document.getElementById('compDescription').value = competency.description;
            document.getElementById('compOrder').value = competency.displayOrder;
            document.getElementById('compActive').checked = competency.isActive;
            document.getElementById('compAdviceHigh').value = competency.adviceHigh;
            document.getElementById('compAdviceLow').value = competency.adviceLow;
            deleteButton.style.display = 'block';
            document.getElementById('compCode').readOnly = true;
        } else {
            // (B) 새 역량 등록
            formTitle.textContent = isChild ? '하위 역량 등록' : '최상위 역량 등록';
            document.getElementById('competencyId').value = '';
            document.getElementById('parentId').value = isChild ? parentId : '';
            document.getElementById('compActive').checked = true;
            document.getElementById('compCode').readOnly = false;
            deleteButton.style.display = 'none';
        }
    }


    /* ==================================================================
    == 2. 문항 목록(R) 및 삭제(D) 로직
    ================================================================== */

    /**
     * 2-1. 특정 역량의 문항 목록을 fetch로 불러와 테이블(tbody)에 렌더링
     * (JS의 'TODO' 주석 부분 구현)
     * @param {Long} competencyId - 역량 ID
     */
    function loadQuestions(competencyId) {
        questionListBody.innerHTML = '<tr><td colspan="6" class="text-center py-4">문항 목록을 불러오는 중...</td></tr>';

        fetch(`/admin/competency/api/competencies/${competencyId}/questions`)
            .then(response => {
                if (!response.ok) throw new Error('문항 목록 로딩 실패');
                return response.json();
            })
            .then(questions => {
                if (questions.length === 0) {
                    questionListBody.innerHTML = '<tr><td colspan="6" class="text-center py-4">연결된 문항이 없습니다.</td></tr>';
                    return;
                }

                // ️ QuestionListDto(JSON)를 HTML(tr)로 변환
                questionListBody.innerHTML = questions.map(q => `
                    <tr>
                        <td>${q.questionCode}</td>
                        <td class="text-start">${q.questionText}</td>
                        <td>${q.questionType}</td>
                        <td>${q.displayOrder}</td>
                        <td>${q.isActive ? '<span class="badge bg-success">활성</span>' : '<span class="badge bg-secondary">비활성</span>'}</td>
                        <td>
                            <button type="button" class="btn btn-outline-secondary btn-sm btn-edit-question" 
                                    data-question-id="${q.id}">
                                수정
                            </button>
                            <button type="button" class="btn btn-outline-danger btn-sm btn-delete-question" 
                                    data-question-id="${q.id}">
                                삭제
                            </button>
                        </td>
                    </tr>
                `).join('');
            })
            .catch(error => {
                console.error(error);
                questionListBody.innerHTML = `<tr><td colspan="6" class="alert alert-danger">${error.message}</td></tr>`;
            });
    }

    /**
     * 2-2. 문항 목록 테이블에서 '삭제' 버튼 클릭 시 (이벤트 위임)
     */
    questionListBody.addEventListener('click', (e) => {
        // (A) '삭제' 버튼을 클릭한 경우
        if (e.target.classList.contains('btn-delete-question')) {
            const button = e.target;
            const questionId = button.dataset.questionId;
            const row = button.closest('tr');
            const questionText = row.cells[1].textContent; // 문항 내용

            if (!confirm(`[${questionText}] 문항을 정말 삭제하시겠습니까?`)) {
                return;
            }

            fetch(`/admin/competency/api/questions/${questionId}`, {
                method: 'DELETE',
                headers: { [csrfHeader]: csrfToken }
            })
                .then(response => {
                    if (!response.ok) return response.json().then(err => { throw new Error(err.error) });
                    return response.json();
                })
                .then(data => {
                    alert(data.message || '삭제되었습니다.');
                    row.remove(); // ️ API 성공 시, 화면에서 해당 줄(tr) 즉시 삭제
                })
                .catch(error => alert('삭제 실패: ' + error.message));
        }

        // (B) '수정' 버튼을 클릭한 경우
        if (e.target.classList.contains('btn-edit-question')) {
            const questionId = e.target.dataset.questionId;
            alert('(구현 필요) 문항 수정 기능 - ID: ' + questionId);
            // TODO: (다음 단계)
            // 1. fetch(`/api/admin/questions/${questionId}/details`) (새 API 필요)
            // 2. 모달(questionModal)을 DTO 데이터로 채우기
            // 3. questionModal.show()
        }
    });


    /* ==================================================================
    == 3. 문항 관리 모달(C/U) 로직
    ================================================================== */

    // 3-1. 모달 DOM 요소
    const questionModalEl = document.getElementById('questionModal');
    const questionModal = new bootstrap.Modal(questionModalEl);
    const questionForm = document.getElementById('questionForm');
    const modalTitle = document.getElementById('questionModalLabel');
    const optionListContainer = document.getElementById('optionListContainer');
    const optionTemplate = document.getElementById('optionRowTemplate');
    const saveQuestionButton = document.getElementById('saveQuestionButton'); // 저장 버튼 캐시

    // 3-2. '새 문항 추가' 버튼 클릭
    document.getElementById('addNewQuestion').addEventListener('click', () => {
        questionForm.reset();
        optionListContainer.innerHTML = '';
        modalTitle.innerHTML = '<i class="fas fa-list-ol me-1"></i> 새 문항 등록';

        const selectedCompetencyId = document.getElementById('competencyId').value;
        if (!selectedCompetencyId) {
            alert("먼저 문항을 추가할 역량을 선택(저장)해야 합니다.");
            return;
        }

        document.getElementById('modalQuestionId').value = '';
        document.getElementById('modalCompetencyId').value = selectedCompetencyId;
        addDefaultOptions('LIKERT_5');
        questionModal.show();
    });

    /**
     * 3-3. 모달(modal)의 '문항 저장' 버튼 클릭 시 (C/U)
     */
    saveQuestionButton.addEventListener('click', (e) => {
        e.preventDefault();

        // 1. '보기' 항목들을 수집
        const options = [];
        const rows = optionListContainer.querySelectorAll('.option-row');
        rows.forEach((row, index) => {
            options.push({
                id: row.querySelector('input[name="optionId"]').value || null,
                optionText: row.querySelector('input[name="optionText"]').value,
                score: parseInt(row.querySelector('input[name="score"]').value, 10),
                displayOrder: parseInt(row.querySelector('input[name="displayOrder"]').value, 10)
            });
        });

        // 2. QuestionFormDto에 맞게 폼 데이터 수집
        const formData = {
            id: document.getElementById('modalQuestionId').value || null,
            competencyId: document.getElementById('modalCompetencyId').value,
            questionText: document.getElementById('modalQuestionText').value,
            questionCode: document.getElementById('modalQuestionCode').value,
            questionType: document.getElementById('modalQuestionType').value,
            displayOrder: parseInt(document.getElementById('modalQuestionOrder').value, 10),
            isActive: document.getElementById('modalQuestionActive').checked,
            options: options //  1번에서 수집한 '보기' 목록
        };

        // 3. API 호출
        fetch('/admin/competency/api/questions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw new Error(err.error) });
                }
                return response.json();
            })
            .then(data => {
                alert(data.message || '문항이 저장되었습니다.');
                questionModal.hide(); //  모달 닫기

                // (중요) 문항 목록(테이블)을 다시 로드
                const competencyId = document.getElementById('competencyId').value;
                if (competencyId) {
                    loadQuestions(competencyId);
                }
            })
            .catch(error => {
                console.error('Question Save Error:', error);
                alert('저장 실패: ' + error.message);
            });
    });

    // 3-4. 모달 '보기 추가'/'보기 삭제' 버튼
    document.getElementById('addOptionButton').addEventListener('click', () => addOptionRow());
    optionListContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('btn-delete-option')) {
            e.target.closest('.option-row').remove();
            // (참고) 삭제 시 updateOptionIndices는 필요 없음. 폼 전송 시점에 수집
        }
    });

    // 3-5. (Helper) 함수들
    function addOptionRow(optionData = null) {
        const newRow = optionTemplate.content.cloneNode(true);
        if (optionData) {
            newRow.querySelector('input[name="optionId"]').value = optionData.id || '';
            newRow.querySelector('input[name="optionText"]').value = optionData.optionText;
            newRow.querySelector('input[name="score"]').value = optionData.score;
            newRow.querySelector('input[name="displayOrder"]').value = optionData.displayOrder;
        }
        optionListContainer.appendChild(newRow);
    }

    function addDefaultOptions(type) {
        if (type === 'LIKERT_5') {
            const defaults = [
                { optionText: '매우 그렇지 않다', score: 1, displayOrder: 1 },
                { optionText: '그렇지 않다', score: 2, displayOrder: 2 },
                { optionText: '보통이다', score: 3, displayOrder: 3 },
                { optionText: '그렇다', score: 4, displayOrder: 4 },
                { optionText: '매우 그렇다', score: 5, displayOrder: 5 },
            ];
            defaults.forEach(addOptionRow);
        }
    }


}); // DOMContentLoaded End