document.addEventListener('DOMContentLoaded', async function() {
    await loadRecords();
    
    document.querySelector('.card .btn-primary').addEventListener('click', function() {
        loadRecords();
    });
    
    document.getElementById('viewRecordModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        const recordId = button.getAttribute('data-id');
        if (recordId) {
            await loadRecordDetail(recordId);
        }
    });
    
    document.getElementById('recordModal').addEventListener('show.bs.modal', async function(event) {
        const button = event.relatedTarget;
        const recordId = button.getAttribute('data-id');
        if (recordId) {
            await loadRecordForEdit(recordId);
        }
    });
});

async function loadRecords() {
    const token = localStorage.getItem('accessToken');
    const field = document.querySelector('.card select')?.value || '';
    const startDate = document.querySelectorAll('.card input[type="date"]')[0]?.value || '';
    const endDate = document.querySelectorAll('.card input[type="date"]')[1]?.value || '';
    const searchText = document.querySelector('.card input[type="text"]')?.value || '';
    
    try {
        const response = await fetch('/api/counseling/records', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            let list = data.content || [];
            
            if (field) list = list.filter(item => item.counselingField === field);
            if (startDate) list = list.filter(item => new Date(item.counselingDate) >= new Date(startDate));
            if (endDate) list = list.filter(item => new Date(item.counselingDate) <= new Date(endDate));
            if (searchText) list = list.filter(item => item.studentName?.includes(searchText));
            
            document.getElementById('totalCount').textContent = `${list.length}건`;
            renderRecordTable(list);
        }
    } catch (error) {
        console.error('상담일지 목록 로드 실패:', error);
    }
}

function renderRecordTable(list) {
    const tbody = document.getElementById('recordTableBody');
    
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">상담일지가 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = list.map(item => `
        <tr>
            <td>${formatDate(item.counselingDate)}</td>
            <td>${item.studentName}</td>
            <td>${getFieldName(item.counselingField)}</td>
            <td>${item.subject || '-'}</td>
            <td>${formatDate(item.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#viewRecordModal" data-id="${item.id}">상세</button>
                <button class="btn btn-sm btn-outline-success" data-bs-toggle="modal" data-bs-target="#recordModal" data-id="${item.id}">수정</button>
            </td>
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

async function loadRecordDetail(recordId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/records/${recordId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const record = await response.json();
            document.getElementById('viewStudentName').textContent = record.studentName || '-';
            document.getElementById('viewCounselingDate').textContent = formatDate(record.counselingDate);
            document.getElementById('viewRecordContent').textContent = record.recordContent || '-';
            document.getElementById('viewCounselorMemo').textContent = record.counselorMemo || '-';
        }
    } catch (error) {
        console.error('상세 정보 로드 실패:', error);
    }
}

async function loadRecordForEdit(recordId) {
    const token = localStorage.getItem('accessToken');
    
    try {
        const response = await fetch(`/api/counseling/records/${recordId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const record = await response.json();
            document.querySelector('#recordModal textarea[placeholder*="상담 내용"]').value = record.recordContent || '';
            document.querySelector('#recordModal textarea[placeholder*="비고"]').value = record.counselorMemo || '';
        }
    } catch (error) {
        console.error('수정 데이터 로드 실패:', error);
    }
}


