const API_BASE = '/api/counseling';

document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
});

function setupEventListeners() {
    document.querySelectorAll('input[name="counselingType"]').forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === 'state') window.location.href = '/counseling/student/status';
        });
    });

    document.querySelectorAll('.btn-outline-primary, .btn-outline-success, .btn-outline-warning, .btn-outline-info').forEach(btn => {
        btn.addEventListener('click', function() {
            const counselingType = this.closest('.card').querySelector('.card-title').textContent.trim();
            openReservationModal(counselingType);
        });
    });
}

function openReservationModal(counselingType) {
    alert(`${counselingType} 신청 기능은 준비 중입니다.`);
}
