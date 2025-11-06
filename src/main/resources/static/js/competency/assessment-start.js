document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('diagnosisForm');
    const questions = form.querySelectorAll('.question-card');
    const totalQuestions = questions.length;

    const progressBar = document.getElementById('progressBar');
    const answeredCountEl = document.getElementById('answeredCount');

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
});