// assessment-history.js

// 1. TOAST UI가 요구하는 데이터 형식으로 변환
// (HTML th:inline에서 선언한 competencyLabels, historyData 변수를 사용)
const chartCategories = competencyLabels;
const chartSeries = historyData.map(row => ({
    name: row.diagnosisTitle, // 예: "2025년"
    data: row.scores        // 예: [4.6, 3.5, 3.8, 2.2]
}));

const chartOptions = {
    chart: {
        width: 'auto',
        height: 350
    },
    yAxis: {
        min: 0,
        max: 5,
        stepSize: 1
    },
    xAxis: {
        title: '핵심역량'
    },
    legend: {
        align: 'bottom'
    }
};

let currentChart = null; // 현재 차트 객체를 저장할 변수

/**
 * 2. 차트를 생성하는 함수 (타입을 인자로 받음)
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

    if (chartType === 'line') {
        currentChart = new toastui.Chart.lineChart({ el, data, options: chartOptions });
    } else {
        currentChart = new toastui.Chart.barChart({ el, data, options: chartOptions });
    }
}

// 3. 버튼 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', () => {
    const btnLine = document.getElementById('btnShowLineChart');
    const btnBar = document.getElementById('btnShowBarChart');

    // 꺾은선 버튼 클릭
    btnLine.addEventListener('click', () => {
        createChart('line');
        btnLine.classList.add('active');
        btnBar.classList.remove('active');
    });

    // 막대 버튼 클릭
    btnBar.addEventListener('click', () => {
        createChart('bar');
        btnBar.classList.add('active');
        btnLine.classList.remove('active');
    });

    // 4. 페이지 첫 로드 시 꺾은선 차트를 기본으로 생성
    if (historyData.length > 0) {
        createChart('line');

        // 차트를 그림과 동시에 '꺾은선' 버튼을 활성화(파란색)시킴
        btnLine.classList.add('active');
        btnBar.classList.remove('active');

    } else {
        // 데이터가 없으면 메시지 표시
        document.getElementById('historyChart').innerHTML =
            '<div class="no-data-message">진단 이력이 없어 차트를 표시할 수 없습니다.</div>';

        // 데이터가 없어도 '꺾은선' 버튼이 기본 활성화 상태가 되도록 설정
        btnLine.classList.add('active');
        btnBar.classList.remove('active');
    }
});