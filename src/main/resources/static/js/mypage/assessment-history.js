// assessment-history.js

// *참고: 이 JS는 Thymeleaf 변수를 가정하고 있지만, Canvas 환경을 위해 임시 데이터를 사용합니다.*
// (반응형 햄버거 메뉴 기능은 Bootstrap 5의 JS/CSS를 통해 HTML에서 처리됩니다.)

// 임시 데이터 (실제 서버에서는 Thymeleaf를 통해 주입될 예정)
const competencyLabels = ['문제해결', '협업능력', '창의적사고', '리더십'];
const historyData = [
    { diagnosisTitle: "2024년 1차 진단", scores: [4.2, 3.8, 4.0, 3.1] },
    { diagnosisTitle: "2024년 2차 진단", scores: [4.6, 4.0, 4.5, 3.5] },
    { diagnosisTitle: "2025년 1차 진단", scores: [4.8, 4.2, 4.6, 3.7] }
];

// TOAST UI Chart 데이터 준비
const chartCategories = competencyLabels;
const chartSeries = historyData.map(row => ({
    name: row.diagnosisTitle,
    data: row.scores
}));

const chartOptions = {
    chart: {
        width: 'auto',
        height: 320 // HTML의 style="height: 320px;"와 맞춤
    },
    yAxis: {
        min: 0,
        max: 5,
        stepSize: 1,
        title: '점수'
    },
    xAxis: {
        title: '핵심역량',
        categories: chartCategories // 범례(categories)를 x축 라벨로 사용
    },
    legend: {
        align: 'bottom'
    },
    tooltip: {
        formatter: (value) => `${value.toFixed(1)}점` // 툴팁 포맷팅
    }
};

let currentChart = null; // 현재 차트 객체를 저장할 변수

/**
 * 차트를 생성하는 함수 (타입을 인자로 받음)
 * @param {'line' | 'bar'} chartType
 */
function createChart(chartType) {
    // 차트를 그릴 컨테이너
    const el = document.getElementById('historyChart');
    // 컨테이너 비우기 (기존 차트 삭제)
    el.innerHTML = '';

    const data = {
        categories: chartCategories,
        series: chartSeries
    };

    if (historyData.length === 0) {
        // 데이터가 없으면 메시지 표시
        el.innerHTML = '<div class="no-data-message"><i class="fas fa-exclamation-circle me-2"></i> 진단 이력이 없어 차트를 표시할 수 없습니다.</div>';
        currentChart = null;
        return;
    }

    // 차트 생성
    try {
        if (chartType === 'line') {
            currentChart = new toastui.Chart.lineChart({ el, data, options: chartOptions });
        } else {
            currentChart = new toastui.Chart.barChart({ el, data, options: chartOptions });
        }
    } catch (error) {
        console.error("TOAST UI Chart 렌더링 오류:", error);
        el.innerHTML = '<div class="no-data-message"><i class="fas fa-exclamation-triangle me-2"></i> 차트 렌더링에 오류가 발생했습니다.</div>';
        currentChart = null;
    }
}

/**
 * 테이블 데이터를 채우는 함수
 */
function populateTable() {
    const tableBody = document.getElementById('scoreTableBody');
    tableBody.innerHTML = ''; // 기존 내용 제거

    // 테이블 헤더 동적 설정
    const tableHeaders = document.querySelectorAll('th[data-label-placeholder]');
    chartCategories.forEach((label, index) => {
         if (tableHeaders[index]) {
            tableHeaders[index].textContent = label;
         }
    });

    if (historyData.length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td colspan="6" class="text-center py-4 text-muted">
                            <i class="fas fa-info-circle me-2"></i> 진단 이력이 없습니다.
                        </td>`;
        tableBody.appendChild(tr);
        return;
    }

    // 테이블 행 채우기
    historyData.forEach(row => {
        const tr = document.createElement('tr');
        // 점수 셀 생성 (소수점 첫째 자리까지 표시)
        let scoreCells = row.scores.map(score => `<td class="text-center">${score.toFixed(1)}</td>`).join('');
        tr.innerHTML = `
            <td class="px-4">${row.diagnosisTitle}</td>
            ${scoreCells}
            <td class="text-center">
                <a href="#" class="btn btn-outline-secondary btn-sm rounded-pill" title="상세보기"><i class="fas fa-search"></i></a>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}


// DOM 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', () => {
    const btnLine = document.getElementById('btnShowLineChart');
    const btnBar = document.getElementById('btnShowBarChart');

    // 테이블 데이터 먼저 채우기
    populateTable();

    // 차트 버튼이 있을 때만 이벤트 리스너 설정
    if (btnLine && btnBar) {
        // 꺾은선 버튼 클릭 이벤트
        btnLine.addEventListener('click', () => {
            createChart('line');
            btnLine.classList.add('active');
            btnBar.classList.remove('active');
        });

        // 막대 버튼 클릭 이벤트
        btnBar.addEventListener('click', () => {
            createChart('bar');
            btnBar.classList.add('active');
            btnLine.classList.remove('active');
        });

        // 페이지 첫 로드 시 꺾은선 차트를 기본으로 생성
        createChart('line');
        // 기본 활성화 상태 설정 (CSS active 클래스 부여)
        btnLine.classList.add('active');
    }
});