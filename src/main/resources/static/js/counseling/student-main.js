document.addEventListener('DOMContentLoaded', function() {
    const goTo = {
        '심리상담': '/counseling/student/psycho',
        '진로상담': '/counseling/student/career',
        '취업상담': '/counseling/student/job',
        '학습상담': '/counseling/student/learning'
    };

    document.querySelectorAll('.btn-outline-primary, .btn-outline-success, .btn-outline-warning, .btn-outline-info')
        .forEach(btn => {
        btn.addEventListener('click', function() {
            const card = btn.closest('.card');
            const counselingType = card.querySelector('.card-title').textContent.trim();
            const url = goTo[counselingType];
            if (url) {
                window.location.href = url;
            } else {
                alert('해당 페이지 이동 기능이 없습니다.');
            }
        });
    });
});
