// 3. HTML/CSS가 모두 준비되면(DOMContentLoaded) 그림 그리기 시작!
document.addEventListener('DOMContentLoaded', () => {

    // 4. 서버 데이터 가져오기 (DTO 필드명: labels, scores, deptScores, univScores)
    const serverChartData = window.assessmentResultData?.radarChartData || null;

    // 5. [수정] 데이터 검증 (myScores -> scores)
    if (!serverChartData || !serverChartData.labels || !serverChartData.scores) {
        console.error("차트 데이터가 비어있거나 형식이 올바르지 않습니다.", serverChartData);
        return;
    }

    // 6. 차트 그릴 캔버스(div) 찾기
    const el = document.getElementById('tui-radar-chart');
    if (!el) {
        console.error("차트를 그릴 div 요소를 찾지 못했습니다. (ID: tui-radar-chart)");
        return;
    }

    const fixedWidth = el.offsetWidth;

    // 7. [수정] 데이터 3종 세트 (myScores -> scores)
    const data = {
        categories: serverChartData.labels,
        series: [
            { name: '나의 점수', data: serverChartData.scores },
            { name: '학과 평균', data: serverChartData.deptScores },
            { name: '학교 평균', data: serverChartData.univScores }
        ]
    };

    // 8. 옵션 (가독성, 5점 만점, 떨림 방지)
    const options = {
        chart: {
            width: fixedWidth,
            height: 450
        },
        legend: { align: 'bottom' },
        series: {
            showDot: true,
            showArea: false // 겹쳐보이게 색 채우기 끄기
        },
        plot: { type: 'radar' },
        yAxis: {
            scale: { min: 0, max: 5 },
            min: 0, max: 5, stepSize: 1
        },
        tooltip: {
            formatter: (value) => Number(value).toFixed(1) + '점'
        },
        exportMenu: { visible: false }
    };

    // 9. 차트 생성
    try {
        new toastui.Chart.radarChart({ el, data, options });
    } catch (e) {
        console.error("TUI 차트 생성에 실패했습니다:", e);
        el.innerText = "차트를 로드하는 중 오류가 발생했습니다.";
    }
});
