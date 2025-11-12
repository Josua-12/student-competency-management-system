/**
 * 메인 대시보드 JavaScript
 */
document.addEventListener('DOMContentLoaded', init);

async function init() {
    try {
        const data = await Api.getJson('/api/dashboard');

        renderUserInfo(data?.userInfo);
        renderCompetencies(data?.competencyScore || []);
        initCompetencyChartFromApi(data?.competencyScore || []);
        renderConsultations(data?.consultations || []);
        renderPrograms(data?.recentPrograms || []);
    } catch (e) {
        if (!getAccessToken()) {
            window.location.href = '/auth/login';
            return;
        }
        console.error(e);
        alert('대시보드 데이터를 불러오지 못했습니다.');
    }
}

function renderUserInfo(u) {
    const name = u?.userName || '';
    const email = u?.userEmail || '';
    const mileage = Number.isFinite(Number(u?.mileage)) ? Number(u.mileage) : 0;
    const programCount = Number.isFinite(Number(u?.programCount)) ? Number(u.programCount) : 0;

    setText('user-initial', name ? name.substring(0,1) : '');
    setText('user-name', name ? `${name}님` : '');
    setText('user-email', email || '');
    setText('user-mileage', `${mileage}점`);
    setText('user-program-count', `${programCount}건`);
}

function renderCompetencies(list) {
    const wrap = document.getElementById('competency-list');
    if (!wrap) return;
    if (!Array.isArray(list) || list.length === 0) {
        wrap.innerHTML = '<p class="no-data">역량 데이터가 없습니다.</p>';
        return;
    }
    wrap.innerHTML = list.map(c => {
        const name = escapeHtml(c?.name || c?.competencyName || '');
        const score = Number(c?.score ?? 0);
        const color = c?.color || '#667eea';
        return `
      <div class="score-item">
        <span class="score-name">${name}</span>
        <div class="score-bar">
          <div class="score-fill" style="width:${score}%;background-color:${color}" title="${score}%"></div>
        </div>
        <span class="score-value">${score.toFixed(0)}%</span>
      </div>
    `;
    }).join('');
}

function renderConsultations(items) {
    const box = document.getElementById('consultation-history');
    if (!box) return;
    if (!Array.isArray(items) || items.length === 0) {
        box.innerHTML = '<p class="no-data">상담 이력이 없습니다.</p>';
        return;
    }
    box.innerHTML = items.map(it => `
    <div class="consult-item">
      <span class="consult-date">${escapeHtml(it.date || '')}</span>
      <span class="consult-title">${escapeHtml(it.title || '')}</span>
    </div>
  `).join('');
}

function renderPrograms(items) {
    const box = document.getElementById('recent-programs');
    if (!box) return;
    if (!Array.isArray(items) || items.length === 0) {
        box.innerHTML = '<p class="no-data">프로그램이 없습니다.</p>';
        return;
    }
    box.innerHTML = items.map(p => `
    <div class="program-item">
      <img src="${escapeAttr(p.imageUrl || '/images/placeholder.jpg')}"
           alt="${escapeAttr(p.title || '프로그램')}"
           class="program-thumb"
           onerror="this.src='/images/placeholder.jpg'">
      <div class="program-info">
        <div class="program-header">
          <span class="program-category" style="background-color:${escapeAttr(p.categoryColor || '#eee')}">
            ${escapeHtml(p.categoryName || '')}
          </span>
          <span class="program-status status-${escapeAttr(String(p.status || '').toLowerCase())}">
            ${escapeHtml(p.status || '')}
          </span>
        </div>
        <h4 class="program-title">${escapeHtml(p.title || '')}</h4>
        <p class="program-desc">${escapeHtml(p.summary || '')}</p>
        <div class="program-footer">
          <span class="program-meta">
            <i class="fas fa-calendar"></i>
            <span>${escapeHtml(p.programStartAtText || '')}</span>
          </span>
          <span class="program-meta">
            <i class="fas fa-users"></i>
            <span>${Number(p.currentParticipants ?? 0)}/${Number(p.capacity ?? 0)}</span>
          </span>
        </div>
      </div>
    </div>
  `).join('');
}

function initCompetencyChartFromApi(competencyScore) {
    const el = document.getElementById('competencyChart');
    if (!el || !window.Chart) return;
    const labels = (competencyScore || []).map(c => c.name || c.competencyName || '');
    const values = (competencyScore || []).map(c => Number(c.score ?? 0));
    const ctx = el.getContext('2d');
    new Chart(ctx, {
        type: 'radar',
        data: {
            labels,
            datasets: [{
                label: '역량 점수',
                data: values,
                backgroundColor: 'rgba(102, 126, 234, 0.2)',
                borderColor: '#667eea',
                borderWidth: 2,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: { r: { min: 0, max: 100, ticks: { stepSize: 20 } } },
            plugins: { legend: { display: false } }
        }
    });
}

function setText(id, v) {
    const el = document.getElementById(id);
    if (el) el.textContent = v;
}
function escapeHtml(s) {
    return String(s || '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
function escapeAttr(s) {
    return escapeHtml(s);
}
