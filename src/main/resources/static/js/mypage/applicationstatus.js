document.addEventListener('DOMContentLoaded', function() {
    const actionButtons = document.querySelectorAll('.action-btn');
    const modal = document.getElementById('custom-modal');
    const modalMessage = document.getElementById('modal-message');
    const modalCloseBtn = document.getElementById('modal-close-btn');

    actionButtons.forEach(button => {
        button.addEventListener('click', function() {
            const action = this.getAttribute('data-action');
            const programRow = this.closest('tr');
            const programName = programRow.getAttribute('data-program');

            if (action === 'cancel') {
                modalMessage.textContent = `"${programName}" 프로그램 신청을 취소하시겠습니까?`;
                modal.style.display = 'flex';
            } else if (action === 'register') {
                modalMessage.textContent = `"${programName}" 프로그램의 산출물을 등록하시겠습니까?`;
                modal.style.display = 'flex';
            }
        });
    });

    modalCloseBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
});
