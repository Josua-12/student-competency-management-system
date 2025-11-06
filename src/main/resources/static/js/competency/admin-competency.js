// (HTML의 <script th:inline="javascript">에서 'initialTreeData' 변수를 선언했다고 가정)

document.addEventListener('DOMContentLoaded', () => {

    /* ==================================================================
    == 1. 역량 트리 및 상세 폼 (기본 로직)
    ================================================================== */

    // 1-1. 상세정보 뷰/플레이스홀더 DOM 캐시
    const detailView = document.getElementById('detailView');
    const placeholder = document.getElementById('detailPlaceholder');
    const form = document.getElementById('competencyForm');
    const formTitle = document.getElementById('formTitle');

    // 1-2. Toast UI Tree 초기화
    const treeContainer = document.getElementById('competencyTree');
    const tree = new tui.Tree(treeContainer, {
        // (위에서 선언한 예시 데이터 또는 서버 데이터)
        data: initialTreeData,
        // 모든 노드를 기본적으로 열린 상태로
        nodeDefaultState: 'opened',
        // Font Awesome 아이콘 사용을 위한 클래스 설정
        nodeIconClass: 'tui-tree-ico-file',
        nodeIconClassOpened: 'tui-tree-ico-opened',
        nodeIconClassClosed: 'tui-tree-ico-closed',
    });

    // 1-3. 트리 노드 선택(클릭) 이벤트 리스너
    tree.on('select', (event) => {
        const nodeId = event.nodeId;
        if (!nodeId) return;

        // 선택된 노드의 전체 데이터 가져오기
        const nodeData = tree.getNode(nodeId);

        // (가짜 데이터 예시. 실제로는 nodeData.data 객체 사용)
        const competency = nodeData.data || {
            name: nodeData.text,
            compCode: `C-${nodeId}`,
            description: `${nodeData.text}에 대한 설명입니다.`,
            displayOrder: 1,
            isActive: true,
            adviceHigh: '강점 설명 예시...',
            adviceLow: '약점 조언 예시...'
        };

        // 폼 채우기 함수 호출
        showDetailView(competency, nodeId, false);
    });

    // 1-4. '최상위 추가' 버튼 클릭 이벤트
    document.getElementById('addNewRootCompetency').addEventListener('click', () => {
        // 폼을 비우고 "새 역량 등록" 모드로 변경
        showDetailView(null, null, false);
    });

    // 1-5. '하위 역량 추가' 버튼 클릭 이벤트
    document.getElementById('addChildButton').addEventListener('click', () => {
        const selectedNodeId = tree.getSelectedNodeId();
        if (!selectedNodeId) {
            alert('하위 역량을 추가할 상위 역량을 왼쪽 트리에서 먼저 선택하세요.');
            return;
        }
        // 폼을 비우고 "새 역량 등록" 모드 (단, parentId는 설정)
        showDetailView(null, selectedNodeId, true);
    });


    /**
     * (Helper) 상세정보 뷰를 표시하고 폼 내용을 채우는 함수
     * @param {object | null} competency - 채울 역량 데이터 (신규 등록 시 null)
     * @param {string | null} parentId - 부모 노드 ID (신규 등록 시)
     * @param {boolean} isChild - 하위 역량 추가인지 여부
     */
    function showDetailView(competency, parentId, isChild) {
        // 1. 뷰 전환
        placeholder.style.display = 'none';
        detailView.style.display = 'block';

        // 2. 폼 리셋 (기존 값 초기화)
        form.reset();

        // 3. 탭을 '기본 정보' 탭으로 강제 이동
        new bootstrap.Tab(document.getElementById('info-tab')).show();

        if (competency) {
            // ----- (A) 기존 역량 수정 -----
            formTitle.textContent = '역량 정보 수정';
            document.getElementById('competencyId').value = competency.id || ''; // (hidden)
            document.getElementById('parentId').value = competency.parentId || ''; // (hidden)
            document.getElementById('compName').value = competency.name || '';
            document.getElementById('compCode').value = competency.compCode || '';
            document.getElementById('compDescription').value = competency.description || '';
            document.getElementById('compOrder').value = competency.displayOrder || 1;
            document.getElementById('compActive').checked = competency.isActive;
            document.getElementById('compAdviceHigh').value = competency.adviceHigh || '';
            document.getElementById('compAdviceLow').value = competency.adviceLow || '';

            // '삭제' 버튼 활성화
            document.getElementById('deleteButton').style.display = 'block';
            document.getElementById('compCode').readOnly = true; // (정책) 코드는 수정 불가

            // TODO: (서버 연동) 이 역량(competency.id)에 해당하는 문항 목록 불러오기
            // fetch(`/api/admin/competency/${competency.id}/questions`)
            //     .then(response => response.json())
            //     .then(questions => { /* ... questionListBody 채우기 ... */ });

        } else {
            // ----- (B) 새 역량 등록 -----
            formTitle.textContent = isChild ? '하위 역량 등록' : '최상위 역량 등록';
            document.getElementById('competencyId').value = ''; // (hidden)
            document.getElementById('parentId').value = isChild ? parentId : ''; // (hidden)
            document.getElementById('compActive').checked = true; // 기본값
            document.getElementById('compCode').readOnly = false;

            // '삭제' 버튼 숨기기
            document.getElementById('deleteButton').style.display = 'none';

            // '문항 관리' 탭 비활성화 (역량이 먼저 생성되어야 함)
            // (구현 방식에 따라 선택)
        }
    }

    /* ==================================================================
    == 2. 문항 관리 모달(Question Modal) 관련 JS
    ================================================================== */

    // 2-1. 모달 DOM 요소 및 템플릿 캐시
    const questionModalEl = document.getElementById('questionModal');
    const questionModal = new bootstrap.Modal(questionModalEl); // Bootstrap 모달 인스턴스

    const questionForm = document.getElementById('questionForm');
    const modalTitle = document.getElementById('questionModalLabel');
    const optionListContainer = document.getElementById('optionListContainer');
    const optionTemplate = document.getElementById('optionRowTemplate');

    // 2-2. '새 문항 추가' 버튼 클릭 시 모달 열기
    // (이 버튼은 '진단 문항 관리' 탭 안에 있습니다)
    document.getElementById('addNewQuestion').addEventListener('click', () => {
        // 폼을 초기화 (이전 값 제거)
        questionForm.reset();
        optionListContainer.innerHTML = ''; // 기존 보기 목록 삭제

        // 모달 제목 변경
        modalTitle.innerHTML = '<i class="fas fa-list-ol me-1"></i> 새 문항 등록';

        // (숨김) 필드 값 설정
        // 현재 선택된 역량의 ID를 가져와서 폼에 설정
        const selectedCompetencyId = document.getElementById('competencyId').value;
        document.getElementById('modalQuestionId').value = ''; // 새 문항이므로 ID 비움
        document.getElementById('modalCompetencyId').value = selectedCompetencyId;

        // (정책) 'LIKERT_5' 선택 시 자동으로 5개 항목 생성 (선택 사항)
        addDefaultOptions('LIKERT_5');

        // 모달 표시
        questionModal.show();
    });

    // 2-3. (구현 필요) '문항 수정' 버튼 클릭 시 모달 열기
    // 예시: document.getElementById('questionListBody').addEventListener('click', (e) => {
    //     if (e.target.classList.contains('btn-edit-question')) {
    //         const questionId = e.target.dataset.id;
    //         // (1) fetch(`/api/admin/questions/${questionId}`)로 문항 + 항목 데이터 가져오기
    //         // (2) questionForm.reset() 및 optionListContainer.innerHTML = ''로 초기화
    //         // (3) 가져온 데이터로 modalTitle, input 값 채우기
    //         // (4) 가져온 '항목' 데이터로 optionListContainer 채우기 (addOptionRow 함수 사용)
    //         // (5) questionModal.show();
    //     }
    // });


    // 2-4. 모달 내부: '보기 항목 추가' 버튼 클릭 이벤트
    document.getElementById('addOptionButton').addEventListener('click', () => {
        addOptionRow(); // 비어있는 새 항목 줄 추가
    });

    // 2-5. 모달 내부: '보기 삭제' 버튼 클릭 이벤트 (이벤트 위임)
    optionListContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('btn-delete-option')) {
            // 클릭된 버튼의 부모 .option-row를 찾아 삭제
            e.target.closest('.option-row').remove();

            // 삭제 후 인덱스 재정렬 (서버 바인딩을 위해 중요)
            updateOptionIndices();
        }
    });

    /**
     * (Helper) 새 '보기' 한 줄을 optionListContainer에 추가하는 함수
     * @param {object | null} optionData - (수정 시) 채워넣을 데이터
     */
    function addOptionRow(optionData = null) {
        // 템플릿 복제
        const newRow = optionTemplate.content.cloneNode(true);
        const inputs = newRow.querySelectorAll('input');

        if (optionData) {
            // (수정) 데이터가 있으면 input 값 채우기
            inputs[0].value = optionData.optionText;
            inputs[1].value = optionData.score;
            inputs[2].value = optionData.displayOrder;
            inputs[3].value = optionData.id; // (숨김) 항목 ID
        }

        // 컨테이너에 추가
        optionListContainer.appendChild(newRow);

        // 인덱스 재정렬 (name 어트리뷰트 업데이트)
        updateOptionIndices();
    }

    /**
     * (Helper) 5점 척도 기본값 추가 함수
     * @param {string} type - 'LIKERT_5'
     */
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
        // (필요시) YES_NO 로직 추가
    }

    /**
     * (Helper) 서버 전송을 위해 '보기' 항목들의 name 인덱스를 재정렬합니다.
     * (예: options[0].optionText, options[1].optionText ...)
     */
    function updateOptionIndices() {
        const rows = optionListContainer.querySelectorAll('.option-row');
        rows.forEach((row, index) => {
            row.querySelector('input[name="optionText"]').name = `options[${index}].optionText`;
            row.querySelector('input[name="score"]').name = `options[${index}].score`;
            row.querySelector('input[name="displayOrder"]').name = `options[${index}].displayOrder`;
            row.querySelector('input[name="optionId"]').name = `options[${index}].id`;
        });
    }

}); // DOMContentLoaded End