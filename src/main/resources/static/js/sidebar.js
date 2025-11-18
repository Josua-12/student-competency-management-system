/* 공통 사이드바 JavaScript */
document.addEventListener('DOMContentLoaded', function() {
    // 메뉴 링크 클릭 시 active 상태 변경
    document.querySelectorAll('.menu-link').forEach(link => {
        link.addEventListener('click', function() {
            document.querySelectorAll('.menu-link')
                .forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // 현재 URL 기반으로 active 메뉴 설정
    function setActiveByLocation() {
        const current = window.location.pathname;
        const links = document.querySelectorAll('.menu-link');
        let matched = null;

        links.forEach(link => {
            const href = link.getAttribute('href');
            if (!href) return;

            const path = href.startsWith('http')
                ? new URL(href, window.location.origin).pathname
                : href;

            if (current.startsWith(path)) {
                if (!matched || path.length > (matched.getAttribute('href') || '').length) {
                    matched = link;
                }
            }
        });

        if (matched) {
            matched.classList.add('active');
        }
    }

    // 페이지 로드 시 active 메뉴 설정
    setActiveByLocation();
});