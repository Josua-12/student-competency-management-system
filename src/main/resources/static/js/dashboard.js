// /js/dashboard.js

// API 유틸리티 객체 정의 (중복 선언 방지)
window.DashboardApi = window.DashboardApi || {
    async getJson(url) {
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API 호출 실패:', error);
            return null;
        }
    }
};

document.addEventListener('DOMContentLoaded', async () => {
    try {
        await Promise.all([
            loadUserInfo(),
            loadCompetency(),
            loadConsultations(),
            loadRecentPrograms()
        ]);
    } catch (e) {
        console.error('대시보드 초기화 실패', e);
    }
});

async function loadUserInfo() {
    const res = await window.DashboardApi.getJson('/api/dashboard/user');
    if (!res) return;
    
    const { name, email, mileage, programCount } = res;
    setText('#user-name', name || '');
    setText('#user-email', email || '');
    setText('#user-mileage', (mileage ?? 0) + '점');
    setText('#user-program-count', (programCount ?? 0) + '건');
    setInitial('#user-initial', name);
}

async function loadCompetency() {
    const res = await window.DashboardApi.getJson('/api/dashboard/competency');
    if (!res) return;
    
    const chartContainer = document.getElementById('competencyChart');
    const listContainer = document.querySelector('#competency-list');
    
    if (res.hasResult && res.labels && res.scores) {
        // 진단 결과가 있을 때 - 차트 표시
        const chartData = {
            labels: res.labels,
            datasets: [{
                label: '역량 점수',
                data: res.scores
            }]
        };
        renderCompetencyChart('competencyChart', chartData);
        
        const listData = res.labels.map((label, index) => ({
            name: label,
            score: res.scores[index] || 0
        }));
        renderCompetencyList('#competency-list', listData);
    } else {
        // 진단 결과가 없을 때 - 진단하기 버튼 표시
        if (chartContainer) {
            chartContainer.style.display = 'none';
        }
        if (listContainer) {
            listContainer.innerHTML = `
                <div class="no-assessment">
                    <p>역량 진단 결과가 없습니다.</p>
                    <a href="/student/assessment" class="btn btn-primary">역량 진단 시작하기</a>
                </div>
            `;
        }
    }
}

async function loadConsultations() {
    const res = await window.DashboardApi.getJson('/api/dashboard/consultations');
    const wrap = document.querySelector('#consultation-history');
    if (!wrap) return;
    
    if (!res || res.length === 0) {
        wrap.innerHTML = '<div class="empty-message">상담 내역이 없습니다.</div>';
        return;
    }
    
    wrap.innerHTML = res.map(toConsultationItem).join('');
}

async function loadRecentPrograms() {
    const res = await window.DashboardApi.getJson('/api/dashboard/programs');
    const wrap = document.querySelector('#recent-programs');
    if (!wrap) return;
    
    if (!res || res.length === 0) {
        wrap.innerHTML = '<div class="empty-message">등록된 프로그램이 없습니다.</div>';
        return;
    }
    
    wrap.innerHTML = res.map(toProgramCard).join('');
}

function setText(sel, v) {
    const el = document.querySelector(sel);
    if (el) el.textContent = v;
}

function setInitial(sel, name) {
    const el = document.querySelector(sel);
    if (!el) return;
    const ch = (name || '').trim().charAt(0);
    el.textContent = ch ? ch.toUpperCase() : '';
}

function renderCompetencyChart(canvasId, data) {
    if (!data) return;
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    
    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: data.labels || [],
            datasets: (data.datasets || []).map(ds => ({
                label: ds.label,
                data: ds.data,
                backgroundColor: 'rgba(102,126,234,0.2)',
                borderColor: '#667eea',
                pointBackgroundColor: '#667eea'
            }))
        },
        options: {
            scales: { r: { beginAtZero: true, suggestedMax: 5 } },
            plugins: { legend: { display: false } },
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

function renderCompetencyList(sel, list) {
    const wrap = document.querySelector(sel);
    if (!wrap) return;
    wrap.innerHTML = (list || []).map(item => `
    <div class="score-item">
      <span class="score-label">${escapeHtml(item.name)}</span>
      <span class="score-value">${Number(item.score ?? 0).toFixed(1)}</span>
    </div>
  `).join('');
}

function toConsultationItem(c) {
    const counselorName = escapeHtml(c.counselorName || '');
    const reservationDate = new Date(c.reservationDate).toLocaleDateString('ko-KR');
    const status = escapeHtml(c.status || '');
    const type = escapeHtml(c.type || '');
    
    return `
    <div class="history-item">
      <div class="history-title">${counselorName} 상담사</div>
      <div class="history-meta">
        <span>${reservationDate}</span>
        <span class="badge">${status}</span>
        <span class="type">${type}</span>
      </div>
    </div>
  `;
}

function toProgramCard(p) {
    const title = escapeHtml(p.title || '');
    const category = escapeHtml(p.category || '');
    const status = escapeHtml(p.status || '');
    const deadline = p.applicationDeadline ? 
        new Date(p.applicationDeadline).toLocaleDateString('ko-KR') : '미정';
    const participants = `${p.currentParticipants || 0}/${p.maxParticipants || 0}`;
    const link = `/noncurricular/program/detail/${encodeURIComponent(p.id)}`;
    
    return `
    <div class="program-card">
      <div class="program-header">
        <div class="program-title">${title}</div>
        <span class="program-category">${category}</span>
      </div>
      <div class="program-info">
        <div class="program-deadline">신청마감: ${deadline}</div>
        <div class="program-participants">참여자: ${participants}</div>
        <span class="program-status">${status}</span>
      </div>
      <a href="${link}" class="program-link">상세보기 →</a>
    </div>
  `;
}

function escapeHtml(s) {
    return (s ?? '').replace(/[&<>"']/g, m => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[m]));
}