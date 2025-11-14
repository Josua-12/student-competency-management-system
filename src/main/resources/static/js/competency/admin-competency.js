document.addEventListener('DOMContentLoaded', () => {

    /* ==================================================================
    == 1. 역량 트리 및 상세 폼
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

    // 1-2. 공통 헤더 생성 함수
    function getAuthHeaders() {
        const token = localStorage.getItem('accessToken');
        return {
            'Content-Type': 'application/json',
            'Authorization': token ? 'Bearer ' + token : ''
        };
    }

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

        // 🚨 [필수 추가] 선택 기능 활성화
        // 이 줄이 없으면 tree.select() 함수가 작동하지 않습니다.
        tree.enableFeature('Selectable', {
            selectedClass: 'tui-tree-selected', // 선택됐을 때 붙을 클래스명
        });

        // 1-5. 트리 노드 선택(클릭) 이벤트
        // tree.on('select', (event) => {
        //
        //     console.log('Select Node Event:', event)
        //     const nodeId = event.nodeId;
        //
        //     const node = tree.getNode(nodeId);
        //
        //     const realDbId = node.data ? node.data.competencyId : null;
        //
        //     if (!realDbId) {
        //         console.error('DB ID를 찾을 수 없습니다.', node);
        //         return;
        //     }
        //
        //     console.log('선택된 DB ID:', realDbId);
        //
        //     // (A) 역량 상세 정보 fetch (기존 로직)
        //     fetch(`/admin/competency/api/competencies/${realDbId}`)
        //         .then(response => {
        //             if (!response.ok) throw new Error('네트워크 응답이 올바르지 않습니다.');
        //             return response.json();
        //         })
        //         .then(competencyDto => {
        //             showDetailView(competencyDto, competencyDto.parentId || '', false);
        //
        //             // (B) 역량 상세 정보 로딩 성공 시,
        //             //     이어서 '문항 목록'을 불러오는 함수 호출
        //             loadQuestions(realDbId);
        //         })
        //         .catch(error => {
        //             console.error('Fetch Error:', error);
        //             alert('상세 정보 로딩 실패: ' + error.message);
        //         });
        // });
// [수정됨] 라이브러리 이벤트 대신 '수동 클릭 이벤트' 사용
        // CSS 충돌로 인해 클릭이 먹통되는 현상을 해결하는 코드입니다.
        // [수정됨] 'tree.getNodeId' 오류 해결 버전
        treeContainer.addEventListener('click', (e) => {
            const target = e.target;

            // 1. 클릭된 요소의 가장 가까운 부모 노드(행) 찾기
            // (TUI Tree에서 각 행은 'tui-tree-node' 클래스를 가진 li 태그입니다)
            const treeNode = target.closest('.tui-tree-node');

            if (!treeNode) return; // 노드가 아니면 무시

            // 2. li 태그의 id 속성값이 곧 Node ID입니다. 바로 가져옵니다.
            const nodeId = treeNode.id;

            if (!nodeId) return;

            console.log('✅ [수동 클릭] Node ID:', nodeId);

            // 3. UI 선택 효과 적용 (파란색 하이라이트)
            tree.select(nodeId);

            // 4. 데이터 가져오기
            const node = tree.getNodeData(nodeId);

            if (!node) {
                console.error('❌ 노드 객체를 찾을 수 없습니다:', nodeId);
                return;
            }

            const nodeData = node.data || {};
            const realDbId = nodeData.competencyId; // 우리가 숨겨둔 진짜 DB ID

            console.log('🔎 DB ID:', realDbId);

            // 5. 상세 정보 로딩
            if (realDbId) {
                fetchDetail(realDbId);
            } else {
                // 최상위 역량 등 data가 없는 경우를 대비해 id 사용
                if (node.id && !isNaN(node.id)) {
                    fetchDetail(node.id);
                }
            }
        });

// (Fetch 로직을 분리해서 깔끔하게 만듦)
        function fetchDetail(id) {
            fetch(`/admin/competency/api/competencies/${id}`)
                .then(res => {
                    if (!res.ok) throw new Error('Network response was not ok');
                    return res.json();
                })
                .then(dto => {
                    showDetailView(dto, dto.parentId || '', false);
                    loadQuestions(id);
                })
                .catch(err => alert('로딩 실패: ' + err.message));
        }
    }

    // 1-6. '최상위 추가' 버튼
    document.getElementById('addNewRootCompetency').addEventListener('click', () => {
        showDetailView(null, null, false);
    });

// 1-7. '하위 역량 추가' 버튼 (수정됨)
    document.getElementById('addChildButton').addEventListener('click', () => {
        // 1. 현재 선택된 트리의 내부 ID 가져오기
        const selectedNodeId = tree.getSelectedNodeId();

        if (!selectedNodeId) {
            alert('하위 역량을 추가할 상위 역량을 왼쪽 트리에서 먼저 선택하세요.');
            return;
        }

        // 2. 내부 ID를 이용해 노드 데이터 가져오기
        // (라이브러리 버전에 따라 getNodeData 또는 getNode 사용)
        const node = tree.getNodeData(selectedNodeId);

        // 3. 진짜 DB ID 추출
        // node.data.competencyId가 있으면 쓰고, 없으면 node.id가 숫자인지 확인해서 씁니다.
        let realDbId = (node && node.data) ? node.data.competencyId : null;

        // [비상 대책] data 안에 없으면 최상위 레벨 등에서 node.id 자체가 DB ID일 수 있음
        if (!realDbId && node.id && !isNaN(node.id)) {
            realDbId = node.id;
        }

        console.log('📌 [하위추가 디버그] 내부ID:', selectedNodeId, '/ DB ID:', realDbId);

        if (!realDbId) {
            alert('선택한 역량의 DB ID를 찾을 수 없습니다. (콘솔 로그 확인 필요)');
            return;
        }

        // 4. 폼 열기 (이제 null이 아닌 진짜 숫자가 들어갑니다)
        showDetailView(null, realDbId, true);

        // 🚨 [추가] 자동 순서 채우기 (현재 자식 개수 + 1)
        const childCount = (node.children) ? node.children.length : 0;
        document.getElementById('compOrder').value = childCount + 1;
    });

    // 1-8. '역량 저장' 버튼 (C/U)
    saveButton.addEventListener('click', (e) => {
        e.preventDefault();

        // 버튼 비활성화 및 로딩 표시
        saveButton.disabled = true;
        const originalSaveButtonText = saveButton.innerHTML;
        saveButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 저장 중...';

        const formData = {
            id: document.getElementById('competencyId').value || null,
            parentId: document.getElementById('parentId').value || null,
            name: document.getElementById('compName').value,
            compCode: document.getElementById('compCode').value,
            description: document.getElementById('compDescription').value,
            displayOrder: parseInt(document.getElementById('compOrder').value, 10),
            active: document.getElementById('compActive').checked,
            adviceHigh: document.getElementById('compAdviceHigh').value,
            adviceLow: document.getElementById('compAdviceLow').value
        };

        fetch('/admin/competency/api/competencies', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert(data.message || '저장되었습니다.');
                window.location.reload();
            })
            .catch(error => {
                alert('저장 실패: ' + error.message);
                saveButton.disabled = false;
                saveButton.innerHTML = originalSaveButtonText
            });
    });

    // 1-9. '역량 삭제' 버튼 (D)
    deleteButton.addEventListener('click', (e) => {
        e.preventDefault();
        const competencyId = document.getElementById('competencyId').value;
        const competencyName = document.getElementById('compName').value;

        if (!competencyId) return alert('삭제할 역량이 선택되지 않았습니다.');
        if (!confirm(`'${competencyName}' 역량을 정말 삭제하시겠습니까?`)) return;

        deleteButton.disabled = true;
        const originalDeleteButtonText = deleteButton.innerHTML;
        deleteButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 삭제 중...';

        fetch(`/admin/competency/api/competencies/${competencyId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) throw new Error(data.error);
                alert(data.message || '삭제되었습니다.');
                window.location.reload();
            })
            .catch(error => {
                alert('삭제 실패: ' + error.message);
                // ⭐️ (2) 실패 시 버튼 원상 복구
                deleteButton.disabled = false;
                deleteButton.innerHTML = originalDeleteButtonText;
            });
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
            document.getElementById('compActive').checked = (competency.active !== undefined) ? competency.active : competency.isActive;
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
                if (!response.ok) {
                    throw new Error('문항 목록을 불러오는데 실패했습니다.');
                }
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
                        <td>${q.active ? '<span class="badge bg-success">활성</span>' : '<span class="badge bg-secondary">비활성</span>'}</td>
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
        const target = e.target;
        // (A) '삭제' 버튼을 클릭한 경우
        if (e.target.classList.contains('btn-delete-question')) {
            const button = target;
            const questionId = button.dataset.questionId;
            const row = button.closest('tr');
            const questionText = row.cells[1].textContent; // 문항 내용

            if (!confirm(`[${questionText}] 문항을 정말 삭제하시겠습니까?`)) {
                return;
            }

            // ️ (1) 버튼 비활성화 (연타 방지)
            button.disabled = true;
            const originalButtonText = button.innerHTML;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';

            fetch(`/admin/competency/api/questions/${questionId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('accessToken')
                }
            })
                .then(response => {
                    if (!response.ok) return response.json().then(err => { throw new Error(err.error) });
                    return response.json();
                })
                .then(data => {
                    alert(data.message || '삭제되었습니다.');
                    row.remove(); // ️ API 성공 시, 화면에서 해당 줄(tr) 즉시 삭제
                })
                .catch(error => {
                    alert('삭제 실패: ' + error.message);
                    // (2) 실패 시 버튼 원상 복구
                    button.disabled = false;
                    button.innerHTML = originalButtonText;
                });
        }

        // (B) '수정' 버튼을 클릭한 경우
        if (target.classList.contains('btn-edit-question')) {
            const button = target;
            const questionId = button.dataset.questionId;

            button.disabled = true;
            const originalEditText = button.innerHTML;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';

            // 1. (11단계 API 호출) 서버에서 '문항 상세정보 + 항목 목록' DTO를 fetch
            fetch(`/admin/competency/api/questions/${questionId}/details`)
                .then(response => {
                    if (!response.ok) return response.json().then(err => { throw new Error(err.error) });
                    return response.json();
                })
                .then(dto => {
                    // 2. 모달 폼 초기화
                    questionForm.reset();
                    optionListContainer.innerHTML = ''; // '보기' 목록 비우기
                    modalTitle.innerHTML = '<i class="fas fa-edit me-1"></i> 문항 수정';

                    // 3. 모달 폼 채우기 (기본 정보)
                    document.getElementById('modalQuestionId').value = dto.id;
                    document.getElementById('modalCompetencyId').value = dto.competencyId;
                    document.getElementById('modalQuestionText').value = dto.questionText;
                    document.getElementById('modalQuestionCode').value = dto.questionCode;
                    document.getElementById('modalQuestionType').value = dto.questionType;
                    document.getElementById('modalQuestionOrder').value = dto.displayOrder;
                    document.getElementById('modalQuestionActive').checked = dto.active;

                    // 4. 모달 폼 채우기 ('보기' 목록)
                    //    서버에서 받은 dto.options 배열을 순회하며 addOptionRow 호출
                    if (dto.options && dto.options.length > 0) {
                        dto.options.forEach(optionData => {
                            addOptionRow(optionData); // ️ (기존 Helper 함수 재사용)
                        });
                    }

                    // 5. 모달 띄우기
                    questionModal.show();
                })
                .catch(error => alert('문항 정보 로딩 실패: ' + error.message))
                .finally(() => {
                    button.disabled = false;
                    button.innerHTML = originalEditText;
                });
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

        saveQuestionButton.disabled = true;
        const originalModalButtonText = saveQuestionButton.innerHTML;
        saveQuestionButton.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 저장 중...';

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
            headers: getAuthHeaders(),
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
            })
            .finally(() => {
                saveQuestionButton.disabled = false;
                saveQuestionButton.innerHTML = originalModalButtonText;
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