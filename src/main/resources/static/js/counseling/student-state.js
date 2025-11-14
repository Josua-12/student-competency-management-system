document.addEventListener('DOMContentLoaded', function() {
    let currentPage = 0;
    let currentReservation = null;

    // 초기 데이터 로드
    loadReservations();

    // 검색 버튼 이벤트
    document.querySelector('.btn-secondary').addEventListener('click', function() {
        currentPage = 0;
        loadReservations();
    });

    // 상담 예약 목록 로드
    function loadReservations() {
        const searchCondition = getSearchCondition();
        
        fetch(`/api/counseling/reservations?${new URLSearchParams(searchCondition)}`)
            .then(response => response.json())
            .then(data => {
                renderReservationTable(data.content);
                renderPagination(data);
                updateTotalCount(data.totalElements);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('데이터를 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 검색 조건 수집
    function getSearchCondition() {
        const dateType = document.querySelector('input[name="dateTypeRadio"]:checked').value;
        const startDate = document.querySelectorAll('input[type="text"]')[0].value;
        const endDate = document.querySelectorAll('input[type="text"]')[1].value;
        const status = document.querySelector('select').value;

        const params = {
            page: currentPage,
            size: 10,
            dateType: dateType
        };

        if (startDate) params.startDate = startDate;
        if (endDate) params.endDate = endDate;
        if (status !== '전체') {
            const statusMap = {
                '대기중': 'PENDING',
                '승인됨': 'CONFIRMED', 
                '완료됨': 'COMPLETED',
                '취소됨': 'CANCELLED'
            };
            params.status = statusMap[status];
        }

        return params;
    }

    // 테이블 렌더링
    function renderReservationTable(reservations) {
        const tbody = document.querySelector('tbody');
        tbody.innerHTML = '';

        reservations.forEach(reservation => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${getFieldDisplayName(reservation.counselingField)}</td>
                <td>${formatDateTime(reservation.createdAt)}</td>
                <td>${reservation.confirmedDate ? formatDate(reservation.confirmedDate) + ' ' + formatTime(reservation.confirmedStartTime) : '-'}</td>
                <td>${reservation.counselorName || '-'}</td>
                <td><span class="badge ${getStatusBadgeClass(reservation.status)}">${getStatusDisplayName(reservation.status)}</span></td>
                <td>${reservation.requestContent || '-'}</td>
                <td>
                    <button class="btn btn-outline-primary btn-sm me-1" onclick="showDetailModal(${reservation.id})">상세</button>
                    ${getActionButton(reservation)}
                </td>
            `;
            tbody.appendChild(row);
        });
    }

    // 상태별 액션 버튼
    function getActionButton(reservation) {
        if (reservation.status === 'PENDING' || reservation.status === 'CONFIRMED') {
            return `<button class="btn btn-outline-danger btn-sm" onclick="showCancelModal(${reservation.id})">취소</button>`;
        } else if (reservation.status === 'COMPLETED') {
            return `<button class="btn btn-outline-success btn-sm" onclick="showSatisfactionModal(${reservation.id})">만족도</button>`;
        }
        return '';
    }

    // 상세 모달 표시
    window.showDetailModal = function(reservationId) {
        fetch(`/api/counseling/reservations/${reservationId}`)
            .then(response => response.json())
            .then(reservation => {
                document.getElementById('detailReservationId').textContent = `CNSL-${reservation.id}`;
                document.getElementById('detailField').textContent = getFieldDisplayName(reservation.counselingField);
                document.getElementById('detailCategory').textContent = reservation.subFieldName || '-';
                document.getElementById('detailRequestedDateTime').textContent = formatDateTime(reservation.reservationDate + 'T' + reservation.startTime);
                document.getElementById('detailConfirmedDateTime').textContent = 
                    reservation.confirmedDate ? formatDateTime(reservation.confirmedDate + 'T' + reservation.confirmedStartTime) : '-';
                document.getElementById('detailCounselor').textContent = reservation.counselorName || '-';
                document.getElementById('detailStatus').textContent = getStatusDisplayName(reservation.status);
                document.getElementById('detailStatus').className = `badge ${getStatusBadgeClass(reservation.status)}`;
                document.getElementById('detailContent').textContent = reservation.requestContent || '-';
                
                if (reservation.memo) {
                    document.getElementById('detailMemoSection').style.display = 'block';
                    document.getElementById('detailMemo').textContent = reservation.memo;
                } else {
                    document.getElementById('detailMemoSection').style.display = 'none';
                }

                new bootstrap.Modal(document.getElementById('detailModal')).show();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('상세 정보를 불러오는 중 오류가 발생했습니다.');
            });
    };

    // 취소 모달 표시
    window.showCancelModal = function(reservationId) {
        currentReservation = reservationId;
        document.getElementById('cancelReason').value = '';
        new bootstrap.Modal(document.getElementById('cancelModal')).show();
    };

    // 만족도 모달 표시
    window.showSatisfactionModal = function(reservationId) {
        currentReservation = reservationId;
        document.getElementById('satisfactionForm').reset();
        new bootstrap.Modal(document.getElementById('satisfactionModal')).show();
    };

    // 취소 확정 버튼 이벤트
    document.querySelector('#cancelModal .btn-danger').addEventListener('click', function() {
        const cancelReason = document.getElementById('cancelReason').value.trim();
        if (!cancelReason) {
            alert('취소 사유를 입력해주세요.');
            return;
        }

        fetch(`/api/counseling/reservations/${currentReservation}/cancel`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ cancelReason: cancelReason })
        })
        .then(response => {
            if (response.ok) {
                alert('상담 예약이 취소되었습니다.');
                bootstrap.Modal.getInstance(document.getElementById('cancelModal')).hide();
                loadReservations();
            } else {
                throw new Error('취소 처리 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('취소 처리 중 오류가 발생했습니다.');
        });
    });

    // 만족도 제출 버튼 이벤트
    document.querySelector('#satisfactionModal .btn-primary').addEventListener('click', function() {
        const formData = new FormData(document.getElementById('satisfactionForm'));
        const satisfactionData = Object.fromEntries(formData);

        // 필수 항목 체크
        if (!satisfactionData.overall || !satisfactionData.expertise || !satisfactionData.helpfulness || !satisfactionData.reuse) {
            alert('모든 필수 항목을 선택해주세요.');
            return;
        }

        const submitData = {
            reservationId: currentReservation,
            overallSatisfaction: parseInt(satisfactionData.overall),
            expertiseSatisfaction: parseInt(satisfactionData.expertise),
            helpfulnessSatisfaction: parseInt(satisfactionData.helpfulness),
            reuseIntention: satisfactionData.reuse,
            additionalFeedback: satisfactionData.additionalFeedback || ''
        };

        fetch('/api/counseling/satisfaction', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submitData)
        })
        .then(response => {
            if (response.ok) {
                alert('만족도 조사가 제출되었습니다.');
                bootstrap.Modal.getInstance(document.getElementById('satisfactionModal')).hide();
            } else {
                throw new Error('만족도 제출 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('만족도 제출 중 오류가 발생했습니다.');
        });
    });

    // 페이지네이션 렌더링
    function renderPagination(pageData) {
        const pagination = document.querySelector('.pagination');
        pagination.innerHTML = '';

        // 이전 버튼
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${pageData.number - 1})">이전</a>`;
        pagination.appendChild(prevLi);

        // 페이지 번호들
        const startPage = Math.max(0, pageData.number - 2);
        const endPage = Math.min(pageData.totalPages - 1, pageData.number + 2);

        for (let i = startPage; i <= endPage; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === pageData.number ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>`;
            pagination.appendChild(li);
        }

        // 다음 버튼
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${pageData.number + 1})">다음</a>`;
        pagination.appendChild(nextLi);
    }

    // 페이지 변경
    window.changePage = function(page) {
        if (page >= 0) {
            currentPage = page;
            loadReservations();
        }
    };

    // 총 개수 업데이트
    function updateTotalCount(total) {
        document.querySelector('.total_count strong').textContent = total;
    }

    // 유틸리티 함수들
    function getFieldDisplayName(field) {
        const fieldMap = {
            'ACADEMIC': '학업 상담',
            'CAREER': '진로 상담', 
            'PSYCHOLOGICAL': '심리 상담',
            'JOB': '취업 상담'
        };
        return fieldMap[field] || field;
    }

    function getStatusDisplayName(status) {
        const statusMap = {
            'PENDING': '대기중',
            'CONFIRMED': '승인됨',
            'COMPLETED': '완료됨',
            'CANCELLED': '취소됨',
            'REJECTED': '거절됨'
        };
        return statusMap[status] || status;
    }

    function getStatusBadgeClass(status) {
        const classMap = {
            'PENDING': 'bg-warning',
            'CONFIRMED': 'bg-primary',
            'COMPLETED': 'bg-success',
            'CANCELLED': 'bg-danger',
            'REJECTED': 'bg-secondary'
        };
        return classMap[status] || 'bg-secondary';
    }

    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return '-';
        const date = new Date(dateTimeStr);
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return date.toLocaleDateString('ko-KR');
    }

    function formatTime(timeStr) {
        if (!timeStr) return '-';
        return timeStr.substring(0, 5);
    }
});