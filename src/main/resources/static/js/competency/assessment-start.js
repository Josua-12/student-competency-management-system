document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('diagnosisForm');
    const questions = form.querySelectorAll('.question-card');
    const totalQuestions = questions.length;

    const progressBar = document.getElementById('progressBar');
    const answeredCountEl = document.getElementById('answeredCount');

    const submitBtn = document.getElementById('submitButton');
    const draftBtn = document.getElementById('draftButton');

    // 폼 내부의 모든 라디오 버튼에 이벤트 리스너 추가
    form.addEventListener('change', updateProgress);

    function updateProgress() {
        // 응답이 완료된 질문의 개수를 셉니다.
        let answeredQuestions = 0;
        questions.forEach((question, index) => {
            // 해당 질문 카드 내부에 체크된 라디오 버튼이 있는지 확인
            if (question.querySelector('input[type="radio"]:checked')) {
                answeredQuestions++;
            }
        });

        // 진행도 업데이트
        const progressPercentage = (answeredQuestions / totalQuestions) * 100;

        progressBar.style.width = progressPercentage + '%';
        progressBar.textContent = Math.round(progressPercentage) + '%';
        answeredCountEl.textContent = answeredQuestions;

        // (선택) 모든 문항에 응답하면 '최종제출' 버튼 활성화
        if (answeredQuestions === totalQuestions) {
            document.getElementById('submitButton').disabled = false;
        } else {
            // (참고: 초기에는 disabled로 설정해둬야 함)
            // document.getElementById('submitButton').disabled = true;
        }
    }

    // 페이지 로드 시 한 번 실행 (임시저장된 값 반영)
    updateProgress();

    /**
     * JWT 전송 및 데이터 수집 로직
     */

    async function sendAssessment(actionType) {
        // 1. JWT 토큰 가져오기
        const token = localStorage.getItem('accessToken');
        if (!token) {
            alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
            window.location.href = '/login';
            return;
        }

        // 2. DTO 매핑을 위한 데이터 수집
        let resultIdVal = null;
        const idInput = form.querySelector('input[name="assessmentResultId"]');
        if (idInput) {
            resultIdVal = idInput.value;
        } else {
            const altInput = form.querySelector('input[name="resultId"]');
            if (altInput) resultIdVal = altInput.value;
        }

        if (!resultIdVal) {
            alert('진단 ID를 찾을 수 없습니다. 페이지를 새로고침 해주세요.');
            return;
        }

        // Responses Map 생성
        const responsesMap = {};
        const checkedRadios = form.querySelectorAll('input[type="radio"]:checked');

        checkedRadios.forEach(radio => {
            const match = radio.name.match(/responses\[(\d+)\]/);
            if (match && match[1]) {
                const questionId = match[1];    // String
                const optionId = radio.value;    // String

                responsesMap[questionId] = parseInt(optionId, 10);
            }
        });

        // 3. 최종 전송 데이터 객체 (AssessmentSubmitDto 구조와 일치해야 함)
        const payload = {
            resultId: parseInt(resultIdVal, 10),
            action: actionType,
            responses: responsesMap
        };

        // 4. Fetch API 호출
        try {
            // 버튼 비활성화 및 로딩 표시 (중복 전송 방지)
            toggleButtons(true);

            const response = await fetch('/user/assessment/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(payload)
            });

            // 5. 응답 처리
            const data = await response.json();

            if (response.ok) {
                // 성공 시 메시지 출력 후 이동
                alert(data.message || (actionType === 'submit' ? '제출되었습니다.' : '저장되었습니다.'));
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    window.location.reload();
                }
            } else {
                // 실패
                alert('처리 실패: ' + (data.message || '알 수 없는 오류가 발생했습니다.'));
                if (data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                }
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 통신 중 오류가 발생했습니다.');
        } finally {
            // 버튼 다시 활성화
            toggleButtons(false);
        }
    }

    // 버튼 활성/비활성 헬퍼 함수
    function toggleButtons(disabled) {
        if (submitBtn) submitBtn.disabled = disabled;
        if (draftBtn) draftBtn.disabled = disabled;
        const spinner = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ';

        if (disabled) {
            if (submitBtn) submitBtn.innerHTML = spinner + '처리중...';
        } else {
            if (submitBtn) submitBtn.innerHTML = '<i class="fas fa-check-circle me-1"></i> 최종제출';
        }
    }

    // 4. 버튼 클릭 이벤트 바인딩

    // 임시저장 버튼
    if (draftBtn) {
        draftBtn.addEventListener('click', function (e) {
            e.preventDefault(); // 폼의 기본 submit 동작 막기
            sendAssessment('save');
        });
    }

    // 최종제출 버튼
    if (submitBtn) {
        submitBtn.addEventListener('click', function (e) {
            e.preventDefault();

            // 미응답 문항 확인
            const answeredCount = document.querySelectorAll('input[type="radio"]:checked').length;

            if (answeredCount < totalQuestions) {
                if (!confirm(`총 ${totalQuestions}문항 중 ${answeredCount}문항만 응답했습니다.\n그래도 제출하시겠습니까? (미응답 문항은 점수에 반영되지 않을 수 있습니다.)`)) {
                    return;
                }
            } else {
                if (!confirm('정말 최종 제출하시겠습니까?\n제출 후에는 수정할 수 없습니다.')) {
                    return;
                }
            }

            sendAssessment('submit');
        });
    }
});