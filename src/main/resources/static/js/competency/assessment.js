document.addEventListener('DOMContentLoaded', function() {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 모든 탭 버튼에서 active 클래스 제거
            tabButtons.forEach(btn => btn.classList.remove('active'));
            // 클릭된 버튼에 active 클래스 추가
            this.classList.add('active');

            // 모든 탭 콘텐츠 숨기기
            tabPanes.forEach(pane => pane.classList.remove('active'));
            // 클릭된 탭 버튼에 해당하는 콘텐츠 보이기
            const targetTabId = this.dataset.tab;
            document.getElementById(targetTabId).classList.add('active');
        });
    });
});