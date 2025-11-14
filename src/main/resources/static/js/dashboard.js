// /js/dashboard.js

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
//        location.href = '/auth/login';
    }
});

async function loadUserInfo() {
    const res = await Api.getJson('/api/dashboard/user'); // Api.get → Api.getJson
    const { name, email, mileage, programCount } = res;
    setText('#user-name', name || '');
    setText('#user-email', email || '');
    setText('#user-mileage', (mileage ?? 0) + '점');
    setText('#user-program-count', (programCount ?? 0) + '건');
    setInitial('#user-initial', name);
}

async function loadCompetency() {
    const res = await Api.getJson('/api/dashboard/competency'); // Api.get → Api.getJson
    renderCompetencyChart('competencyChart', res.chart);
    renderCompetencyList('#competency-list', res.list);
}

async function loadConsultations() {
    const res = await Api.getJson('/api/dashboard/consultations'); // Api.get → Api.getJson
    const wrap = document.querySelector('#consultation-history');
    wrap.innerHTML = (res || []).map(toConsultationItem).join('');
}

async function loadRecentPrograms() {
    const res = await Api.getJson('/api/dashboard/programs'); // Api.get → Api.getJson
    const wrap = document.querySelector('#recent-programs');
    wrap.innerHTML = (res || []).map(toProgramCard).join('');
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
    const date = escapeHtml(c.date || '');
    const title = escapeHtml(c.title || '');
    const status = escapeHtml(c.status || '');
    return `
    <div class="history-item">
      <div class="history-title">${title}</div>
      <div class="history-meta">
        <span>${date}</span>
        <span class="badge">${status}</span>
      </div>
    </div>
  `;
}

function toProgramCard(p) {
    const title = escapeHtml(p.title || '');
    const period = escapeHtml(p.period || '');
    const link = `/programs/${encodeURIComponent(p.id)}`;
    return `
    <a class="program-card" href="${link}">
      <div class="program-title">${title}</div>
      <div class="program-period">${period}</div>
    </a>
  `;
}

function escapeHtml(s) {
    return (s ?? '').replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}
