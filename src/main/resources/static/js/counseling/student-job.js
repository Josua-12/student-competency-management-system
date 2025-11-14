document.addEventListener('DOMContentLoaded', function() {
    let currentDate = new Date();
    let selectedSubfield = 'all';
    let monthSchedules = {};

    initializeCalendar();
    initializeSubfieldTabs();

    function initializeSubfieldTabs() {
        const tabs = document.querySelectorAll('#subfieldTabs button');
        tabs.forEach(tab => {
            tab.addEventListener('click', function() {
                tabs.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
                selectedSubfield = this.dataset.subfield;
                loadMonthSchedules();
            });
        });
    }

    function initializeCalendar() {
        document.getElementById('prevMonth').addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() - 1);
            renderCalendar();
            loadMonthSchedules();
        });

        document.getElementById('nextMonth').addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() + 1);
            renderCalendar();
            loadMonthSchedules();
        });

        renderCalendar();
        loadMonthSchedules();
    }

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        
        document.getElementById('currentMonth').textContent = `${year}/${String(month + 1).padStart(2, '0')}`;
        
        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();
        const prevLastDate = new Date(year, month, 0).getDate();
        
        const tbody = document.getElementById('calendarBody');
        tbody.innerHTML = '';
        
        let date = 1;
        let nextDate = 1;
        
        for (let i = 0; i < 6; i++) {
            const row = document.createElement('tr');
            
            for (let j = 0; j < 7; j++) {
                const cell = document.createElement('td');
                cell.className = 'text-center p-2';
                cell.style.cursor = 'pointer';
                cell.style.minHeight = '80px';
                cell.style.verticalAlign = 'top';
                
                if (i === 0 && j < firstDay) {
                    cell.textContent = prevLastDate - firstDay + j + 1;
                    cell.classList.add('text-muted');
                } else if (date > lastDate) {
                    cell.textContent = nextDate++;
                    cell.classList.add('text-muted');
                } else {
                    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}`;
                    cell.innerHTML = `<div class="fw-bold">${date}</div>`;
                    cell.dataset.date = dateStr;
                    
                    cell.addEventListener('click', function() {
                        if (!this.classList.contains('text-muted')) {
                            showScheduleList(this.dataset.date);
                        }
                    });
                    
                    date++;
                }
                
                row.appendChild(cell);
            }
            
            tbody.appendChild(row);
            if (date > lastDate) break;
        }
    }

    function loadMonthSchedules() {
        monthSchedules = {};
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        const startDate = new Date(year, month, 1);
        const endDate = new Date(year, month + 1, 0);
        
        const params = new URLSearchParams({
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0],
            field: 'EMPLOYMENT'
        });
        
        if (selectedSubfield !== 'all') {
            params.append('subfieldId', selectedSubfield);
        }
        
        fetch(`/api/counseling/schedule/monthly?${params}`)
            .then(response => response.json())
            .then(schedules => {
                schedules.forEach(schedule => {
                    const dateKey = schedule.date;
                    if (!monthSchedules[dateKey]) {
                        monthSchedules[dateKey] = [];
                    }
                    monthSchedules[dateKey].push(schedule);
                });
                updateCalendarDisplay();
            })
            .catch(error => console.error('Error:', error));
    }

    function updateCalendarDisplay() {
        const cells = document.querySelectorAll('#calendarBody td[data-date]');
        cells.forEach(cell => {
            const dateStr = cell.dataset.date;
            const schedules = monthSchedules[dateStr] || [];
            
            if (schedules.length > 0) {
                const available = schedules.filter(s => s.isAvailable);
                const unavailable = schedules.filter(s => !s.isAvailable);
                
                let html = `<div class="fw-bold">${new Date(dateStr).getDate()}</div>`;
                
                const displayCount = 2;
                const toDisplay = schedules.slice(0, displayCount);
                
                toDisplay.forEach(schedule => {
                    const color = schedule.isAvailable ? 'success' : 'danger';
                    const status = schedule.isAvailable ? '예약가능' : '예약불가';
                    html += `<small class="d-block text-${color}">${schedule.startTime} ${schedule.subfieldName} (${status})</small>`;
                });
                
                if (schedules.length > displayCount) {
                    html += `<small class="d-block text-primary fw-bold">더보기+${schedules.length - displayCount}</small>`;
                }
                
                cell.innerHTML = html;
                
                if (available.length > 0) {
                    cell.style.backgroundColor = '#d4edda';
                } else {
                    cell.style.backgroundColor = '#f8d7da';
                }
            }
        });
    }

    function showScheduleList(dateStr) {
        const schedules = monthSchedules[dateStr] || [];
        const date = new Date(dateStr);
        const dateTitle = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} (${['일','월','화','수','목','금','토'][date.getDay()]})`;
        
        document.getElementById('selectedDateTitle').textContent = dateTitle;
        
        const listContainer = document.getElementById('scheduleList');
        
        if (schedules.length === 0) {
            listContainer.innerHTML = '<div class="text-center text-muted py-5"><p>해당 날짜에 일정이 없습니다</p></div>';
            return;
        }
        
        let html = '<div class="list-group">';
        schedules.forEach(schedule => {
            const isAvailable = schedule.isAvailable;
            const statusClass = isAvailable ? 'success' : 'secondary';
            const statusText = isAvailable ? '예정' : '예약불가';
            
            html += `
                <div class="list-group-item">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <span class="badge bg-${statusClass}">${statusText}</span>
                        <span class="fw-bold">${schedule.startTime}</span>
                    </div>
                    <p class="mb-1 small">컨설팅유형: ${schedule.subfieldName}</p>
                    <p class="mb-2 small">컨설턴트: ${schedule.counselorName}</p>
                    ${isAvailable ? `<button class="btn btn-warning btn-sm w-100" onclick="applySchedule('${dateStr}', '${schedule.startTime}', ${schedule.counselorId}, ${schedule.subfieldId}, '${schedule.subfieldName}')">신청하기</button>` : ''}
                </div>
            `;
        });
        html += '</div>';
        
        listContainer.innerHTML = html;
    }

    window.applySchedule = function(date, time, counselorId, subfieldId, subfieldName) {
        document.getElementById('counselingDate').value = date;
        document.getElementById('counselingDate').readOnly = true;
        
        const timeSelect = document.getElementById('counselingTime');
        timeSelect.innerHTML = `<option value="${time}">${time}</option>`;
        timeSelect.disabled = true;
        
        const categorySelect = document.getElementById('counselingCategory');
        categorySelect.dataset.subfieldId = subfieldId;
        
        const modal = new bootstrap.Modal(document.getElementById('reservationModal'));
        modal.show();
    };
});
