let currentCounselorId = null;
let currentField = null;

document.addEventListener('DOMContentLoaded', async function() {
    await loadCounselors();
    await loadSubFields();
    await loadSatisfactionQuestions();
});

async function toggleCounselorStatus(userId, isActive) {
    if (!confirm(`상담원을 ${isActive ? '활성' : '비활성'}화 하시겠습니까?`)) return;
    
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/users/${userId}/status`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({isActive})
        });
        
        if (response.ok) {
            alert('상태가 변경되었습니다.');
            loadCounselors();
        } else {
            const error = await response.json();
            alert(error.message || '상태 변경에 실패했습니다.');
        }
    } catch (error) {
        console.error('상태 변경 실패:', error);
        alert('상태 변경 중 오류가 발생했습니다.');
    }
}

async function editCounselor(userId) {
    alert('상담원 수정 기능은 추후 구현 예정입니다.');
    return;
    // TODO: 상담원 상세 조회 API 미구현
    /* try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/counseling/management/counselors/${userId}`, {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const counselor = await response.json();
            currentCounselorId = userId;
            
            const modal = document.getElementById('counselorModal');
            modal.querySelector('.modal-title').textContent = '상담원 수정';
            modal.querySelectorAll('input[type="text"]')[0].value = counselor.name;
            modal.querySelector('input[type="email"]').value = counselor.email;
            modal.querySelector('select').value = counselor.counselingField;
            modal.querySelector('textarea').value = counselor.specialization || '';
            modal.querySelector('#counselorActive').checked = counselor.isActive;
            
            new bootstrap.Modal(modal).show();
        }
    } catch (error) {
        console.error('상담원 정보 로드 실패:', error);
        alert('상담원 정보를 불러오는 중 오류가 발생했습니다.');
    }
}

async function loadCounselors() {
    try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch('/api/counseling/management/counselors', {
            headers: {'Authorization': `Bearer ${token}`}
        });
        
        if (response.ok) {
            const data = await response.json();
            const counselors = data.content || data;
            renderCounselorTable(counselors);
        }
    } catch (error) {
        console.error('상담원 목록 로드 실패:', error);
    }
}

function renderCounselorTable(counselors) {
    const tbody = document.querySelector('#counselorTable tbody');
    if (!tbody) return;
    
    if (counselors.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">등록된 상담원이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = counselors.map(c => `
        <tr data-field="${c.counselingField}" data-user-id="${c.userId}">
            <td>${c.name}</td>
            <td>${c.email}</td>
            <td>${getFieldName(c.counselingField)}</td>
            <td>${c.specialization || '-'}</td>
            <td><span class="badge bg-${c.isActive ? 'success' : 'danger'}">${c.isActive ? '활성' : '비활성'}</span></td>
            <td>${formatDate(c.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="editCounselor(${c.userId})">수정</button>
                <button class="btn btn-sm btn-outline-${c.isActive ? 'warning' : 'success'}" 
                        onclick="toggleCounselorStatus(${c.userId}, ${!c.isActive})">
                    ${c.isActive ? '비활성' : '활성'}
                </button>
            </td>
        </tr>
    `).join('');
}

document.getElementById('counselorModal').addEventListener('hidden.bs.modal', function() {
    currentCounselorId = null;
    this.querySelector('.modal-title').textContent = '상담원 등록/수정';
    this.querySelectorAll('input[type="text"]')[0].value = '';
    this.querySelector('input[type="email"]').value = '';
    this.querySelector('select').value = '';
    this.querySelector('textarea').value = '';
    this.querySelector('#counselorActive').checked = true;
});

document.getElementById('saveCounselor').addEventListener('click', async function() {
    const modal = document.getElementById('counselorModal');
    const name = modal.querySelectorAll('input[type="text"]')[0].value;
    const email = modal.querySelector('input[type="email"]').value;
    const field = modal.querySelector('select').value;
    const specialization = modal.querySelector('textarea').value;
    const isActive = modal.querySelector('#counselorActive').checked;
    
    if (!name.trim() || !email.trim() || !field) {
        alert('필수 항목을 모두 입력해주세요.');
        return;
    }
    
    try {
        const token = localStorage.getItem('accessToken');
        const url = currentCounselorId ? 
            `/api/counseling/management/counselors/${currentCounselorId}` :
            '/api/counseling/management/counselors';
        const method = currentCounselorId ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method,
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({name, email, counselingField: field, specialization, isActive})
        });
        
        if (response.ok) {
            alert('저장되었습니다.');
            bootstrap.Modal.getInstance(modal).hide();
            loadCounselors();
        } else {
            const error = await response.json();
            alert(error.message || '저장에 실패했습니다.');
        }
    } catch (error) {
        console.error('저장 실패:', error);
        alert('저장 중 오류가 발생했습니다.');
    }
});

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

async function loadSubFields() {
    // TODO: API 미구현 - 빈 데이터로 표시
    ['psychologicalList', 'careerList', 'employmentList', 'learningList'].forEach(id => {
        document.getElementById(id).innerHTML = '<div class="text-center p-3">등록된 세부분류가 없습니다.</div>';
    });
    // const token = localStorage.getItem('accessToken');
    // try {
    //     const response = await fetch('/api/counseling/subfields', {
    //         headers: {'Authorization': `Bearer ${token}`}
    //     });
    //     if (response.ok) {
    //         const data = await response.json();
    //         renderSubFields(data);
    //     }
    // } catch (error) {
    //     console.error('세부분류 로드 실패:', error);
    // }
}

function renderSubFields(data) {
    const fields = ['PSYCHOLOGICAL', 'CAREER', 'EMPLOYMENT', 'LEARNING'];
    const listIds = ['psychologicalList', 'careerList', 'employmentList', 'learningList'];
    
    fields.forEach((field, index) => {
        const list = document.getElementById(listIds[index]);
        const items = data.filter(item => item.field === field);
        
        if (items.length === 0) {
            list.innerHTML = '<div class="text-center p-3">등록된 세부분류가 없습니다.</div>';
        } else {
            list.innerHTML = items.map(item => `
                <div class="list-group-item d-flex justify-content-between align-items-center">
                    <span>${item.name}</span>
                    <div>
                        <div class="form-check form-switch d-inline-block me-2">
                            <input class="form-check-input" type="checkbox" ${item.isActive ? 'checked' : ''} 
                                   onchange="toggleSubfieldStatus(${item.id}, this.checked)">
                        </div>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteSubfield(${item.id})">삭제</button>
                    </div>
                </div>
            `).join('');
        }
    });
}

async function toggleSubfieldStatus(id, isActive) {
    alert('세부분류 상태 변경 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 미구현
}

async function deleteSubfield(id) {
    alert('세부분류 삭제 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 미구현
}

document.querySelectorAll('[data-bs-target="#subfieldModal"]').forEach(button => {
    button.addEventListener('click', function() {
        currentField = this.getAttribute('data-field');
        const fieldNames = {
            'PSYCHOLOGICAL': '심리상담',
            'CAREER': '진로상담',
            'EMPLOYMENT': '취업상담',
            'LEARNING': '학습상담'
        };
        document.getElementById('parentField').value = fieldNames[currentField] || currentField;
    });
});

document.getElementById('saveSubfield')?.addEventListener('click', async function() {
    alert('세부분류 저장 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 미구현
});

async function loadSatisfactionQuestions() {
    // TODO: API 미구현 - 빈 데이터로 표시
    document.getElementById('satisfactionQuestions').innerHTML = 
        '<tr><td colspan="7" class="text-center">등록된 문항이 없습니다.</td></tr>';
    // try {
    //     const token = localStorage.getItem('accessToken');
    //     const fieldFilter = document.getElementById('fieldFilter')?.value || 'ALL';
    //     const url = fieldFilter && fieldFilter !== 'ALL' ? 
    //         `/api/counseling/satisfaction/questions?field=${fieldFilter}` :
    //         '/api/counseling/satisfaction/questions';
    //     const response = await fetch(url, {
    //         headers: {'Authorization': `Bearer ${token}`}
    //     });
    //     if (response.ok) {
    //         const questions = await response.json();
    //         renderSatisfactionQuestions(questions);
    //     }
    // } catch (error) {
    //     console.error('만족도 문항 로드 실패:', error);
    // }
}

function renderSatisfactionQuestions(questions) {
    const tbody = document.getElementById('satisfactionQuestions');
    if (questions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">등록된 문항이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = questions.map(q => {
        const fieldBadge = q.field === 'ALL' ? 'bg-secondary' : 
                          q.field === 'PSYCHOLOGICAL' ? 'bg-info' :
                          q.field === 'CAREER' ? 'bg-warning' :
                          q.field === 'EMPLOYMENT' ? 'bg-success' : 'bg-primary';
        const fieldText = q.field === 'ALL' ? '공통' : getFieldName(q.field);
        const typeText = q.type === 'RATING' ? '5점 척도' : 
                        q.type === 'MULTIPLE_CHOICE' ? '객관식' : '주관식';
        const isSystem = q.isSystemDefault;
        const buttons = isSystem ? 
            `<button class="btn btn-sm btn-outline-secondary" disabled>수정 불가</button>
             <button class="btn btn-sm btn-outline-secondary" disabled>삭제 불가</button>` :
            `<button class="btn btn-sm btn-outline-danger" onclick="deleteQuestion(${q.id})">삭제</button>`;
        
        return `
            <tr>
                <td>${q.questionOrder}</td>
                <td><span class="badge ${fieldBadge}">${fieldText}</span></td>
                <td>${q.questionText}</td>
                <td>${typeText}</td>
                <td><i class="bi bi-${isSystem ? 'check' : 'x'}-circle text-${isSystem ? 'success' : 'muted'}"></i></td>
                <td><span class="badge bg-${q.isRequired ? 'success' : 'secondary'}">${q.isRequired ? '필수' : '선택'}</span></td>
                <td>${buttons}</td>
            </tr>
        `;
    }).join('');
}

async function deleteQuestion(id) {
    alert('문항 삭제 기능은 API 구현 후 사용 가능합니다.');
    // TODO: API 미구현
}

document.getElementById('fieldFilter')?.addEventListener('change', function() {
    loadSatisfactionQuestions();
});

document.getElementById('counselorFieldFilter')?.addEventListener('change', function() {
    const selectedField = this.value;
    const rows = document.querySelectorAll('#counselorTable tbody tr');
    
    rows.forEach(row => {
        if (!selectedField || row.getAttribute('data-field') === selectedField) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
});

window.toggleCounselorStatus = toggleCounselorStatus;
window.editCounselor = editCounselor;
window.toggleSubfieldStatus = toggleSubfieldStatus;
window.deleteSubfield = deleteSubfield;
window.deleteQuestion = deleteQuestion;
