document.addEventListener('DOMContentLoaded', async function() {
    await loadSatisfactionData();
    
    document.querySelector('.card .btn-primary').addEventListener('click', function() {
        loadSatisfactionData();
    });
});

async function loadSatisfactionData() {
    const token = localStorage.getItem('accessToken');
    const field = document.getElementById('counselingType')?.value || '';
    const startDate = document.querySelectorAll('.card input[type="date"]')[0]?.value || '';
    const endDate = document.querySelectorAll('.card input[type="date"]')[1]?.value || '';
    
    try {
        const response = await fetch('/api/counseling/satisfaction/counselor/summary', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            document.getElementById('avgSatisfaction').textContent = `${data.avgSatisfaction || 0}/5.0`;
            document.getElementById('responseRate').textContent = `${data.responseRate || 0}%`;
            document.getElementById('totalResponses').textContent = `${data.totalResponses || 0}건`;
            document.getElementById('reusageRate').textContent = `${data.reusageRate || 0}%`;
        }
        
        const listResponse = await fetch('/api/counseling/satisfaction/counselor/list', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (listResponse.ok) {
            const listData = await listResponse.json();
            let list = listData.content || [];
            
            if (field) list = list.filter(item => item.counselingField === field);
            if (startDate) list = list.filter(item => new Date(item.counselingDate) >= new Date(startDate));
            if (endDate) list = list.filter(item => new Date(item.counselingDate) <= new Date(endDate));
            
            renderSatisfactionTable(list);
        }
        
        const questionResponse = await fetch('/api/counseling/satisfaction/counselor/questions', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (questionResponse.ok) {
            const questions = await questionResponse.json();
            renderQuestionResults(questions);
        }
    } catch (error) {
        console.error('만족도 데이터 로드 실패:', error);
    }
}

function renderSatisfactionTable(list) {
    const tbody = document.getElementById('satisfactionTableBody');
    
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">만족도 조사 결과가 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = list.map(item => `
        <tr>
            <td>${formatDate(item.counselingDate)}</td>
            <td>${item.studentName}</td>
            <td>${getFieldName(item.counselingField)}</td>
            <td>${item.overallSatisfaction || '-'}</td>
            <td>${item.professionalism || '-'}</td>
            <td>${item.helpfulness || '-'}</td>
            <td>${item.willReuse ? '예' : '아니오'}</td>
            <td>${formatDate(item.submittedAt)}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="viewSatisfactionDetail(${item.id})">상세</button>
            </td>
        </tr>
    `).join('');
}

function renderQuestionResults(questions) {
    const tbody = document.querySelector('.card:nth-of-type(4) tbody');
    
    if (!questions || questions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">문항별 결과가 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = questions.map(q => `
        <tr>
            <td>${q.questionText}</td>
            <td><strong>${q.avgScore || 0}</strong></td>
            <td>${q.score5Count || 0}건 (${q.score5Percent || 0}%)</td>
            <td>${q.score4Count || 0}건 (${q.score4Percent || 0}%)</td>
            <td>${q.score3Count || 0}건 (${q.score3Percent || 0}%)</td>
            <td>${q.score2Count || 0}건 (${q.score2Percent || 0}%)</td>
            <td>${q.score1Count || 0}건 (${q.score1Percent || 0}%)</td>
            <td>${q.responseCount || 0}/${q.totalCount || 0} (${q.responseRate || 0}%)</td>
        </tr>
    `).join('');
}

function getFieldName(field) {
    const fieldNames = {
        'PSYCHOLOGICAL': '심리상담',
        'CAREER': '진로상담',
        'EMPLOYMENT': '취업상담',
        'LEARNING': '학습상담'
    };
    return fieldNames[field] || field;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR');
}

async function viewSatisfactionDetail(satisfactionId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/satisfaction/${satisfactionId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const detail = await response.json();
            renderSatisfactionDetailModal(detail);
            const modal = new bootstrap.Modal(document.getElementById('satisfactionDetailModal'));
            modal.show();
        }
    } catch (error) {
        console.error('만족도 상세 조회 실패:', error);
        alert('만족도 상세 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

function renderSatisfactionDetailModal(detail) {
    const modalBody = document.querySelector('#satisfactionDetailModal .modal-body');
    modalBody.innerHTML = `
        <div class="row mb-3">
            <div class="col-md-6">
                <strong>학생명:</strong> ${detail.studentName} (${detail.studentNumber})
            </div>
            <div class="col-md-6">
                <strong>상담일:</strong> ${formatDate(detail.counselingDate)}
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <strong>상담유형:</strong> ${getFieldName(detail.counselingField)}
            </div>
            <div class="col-md-6">
                <strong>제출일:</strong> ${formatDate(detail.submittedAt)}
            </div>
        </div>
        <hr>
        ${detail.answers.map((answer, index) => `
            <div class="mb-4">
                <h6><strong>${index + 1}. ${answer.questionText}</strong></h6>
                <p class="ms-3">
                    ${answer.answerType === 'RATING' ? 
                        `<span class="badge ${getRatingBadge(answer.ratingValue)} fs-6">${answer.ratingValue}점</span>` :
                        answer.answerType === 'TEXT' ? 
                        `<div class="border p-3 bg-light">${answer.answerText || '-'}</div>` :
                        `<span class="badge bg-info">${answer.selectedOptionText}</span>`
                    }
                </p>
            </div>
        `).join('')}
    `;
}

function getRatingBadge(rating) {
    if (rating >= 4.5) return 'bg-success';
    if (rating >= 3.5) return 'bg-primary';
    if (rating >= 2.5) return 'bg-warning';
    return 'bg-danger';
}

window.viewSatisfactionDetail = viewSatisfactionDetail;
