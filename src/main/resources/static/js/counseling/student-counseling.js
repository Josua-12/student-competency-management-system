document.addEventListener('DOMContentLoaded', function() {
    let currentWeekStart = getMonday(new Date());
    let selectedDate = null;
    let selectedSlot = null;
    let counselingField = getCounselingField();
    let weekSlotMap = {};

    function getCounselingField() {
        const path = window.location.pathname;
        if (path.includes('psycho')) return 'PSYCHOLOGICAL';
        if (path.includes('career')) return 'CAREER';
        if (path.includes('learning')) return 'ACADEMIC';
        return 'PSYCHOLOGICAL';
    }

    function getMonday(date) {
        const d = new Date(date);
        const day = d.getDay();
        const diff = d.getDate() - day + (day === 0 ? -6 : 1);
        return new Date(d.setDate(diff));
    }

    function getInitialWeekStart() {
        const now = new Date();
        const dayOfWeek = now.getDay();
        const hour = now.getHours();
        
        if (dayOfWeek === 5 && hour >= 17) {
            const nextMonday = new Date(now);
            nextMonday.setDate(now.getDate() + (8 - dayOfWeek));
            return getMonday(nextMonday);
        }
        
        return getMonday(now);
    }

    currentWeekStart = getInitialWeekStart();
    initCalendar();
    
    document.getElementById('prevWeek')?.addEventListener('click', function() {
        currentWeekStart.setDate(currentWeekStart.getDate() - 7);
        renderCalendar();
    });

    document.getElementById('nextWeek')?.addEventListener('click', function() {
        currentWeekStart.setDate(currentWeekStart.getDate() + 7);
        renderCalendar();
    });

    const submitBtn = document.querySelector('.modal-footer .btn-primary') || document.querySelector('.modal-footer .btn-success');
    if (submitBtn) {
        submitBtn.addEventListener('click', submitReservation);
    }

    function initCalendar() {
        renderCalendar();
    }

    function renderCalendar() {
        const weekEnd = new Date(currentWeekStart);
        weekEnd.setDate(weekEnd.getDate() + 4);
        
        const weekRangeEl = document.getElementById('weekRange');
        if (weekRangeEl) {
            weekRangeEl.textContent = `${currentWeekStart.getFullYear()}년 ${currentWeekStart.getMonth() + 1}월 ${currentWeekStart.getDate()}일 - ${weekEnd.getMonth() + 1}월 ${weekEnd.getDate()}일`;
        }
        
        const tbody = document.getElementById('scheduleBody');
        if (!tbody) return;
        
        const oldListener = tbody._clickListener;
        if (oldListener) {
            tbody.removeEventListener('click', oldListener);
        }
        
        tbody.innerHTML = '';
        
        const timeSlots = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'];
        const now = new Date();
        
        timeSlots.forEach(time => {
            const row = document.createElement('tr');
            const timeCell = document.createElement('td');
            timeCell.className = 'text-center align-middle';
            timeCell.textContent = time;
            row.appendChild(timeCell);
            
            for (let i = 0; i < 5; i++) {
                const date = new Date(currentWeekStart);
                date.setDate(date.getDate() + i);
                const dateStr = date.toISOString().split('T')[0];
                const slotKey = `${dateStr}_${time}`;
                
                const cell = document.createElement('td');
                cell.className = 'text-center align-middle slot-cell';
                cell.style.padding = '10px';
                cell.dataset.date = dateStr;
                cell.dataset.time = time;
                cell.dataset.slotKey = slotKey;
                
                const isPast = new Date(`${dateStr}T${time}`) < now;
                
                if (isPast) {
                    cell.style.backgroundColor = '#f8f9fa';
                    cell.style.color = '#adb5bd';
                    cell.style.cursor = 'not-allowed';
                    cell.textContent = '상담사 선택';
                    cell.classList.add('disabled');
                } else {
                    cell.textContent = '상담사 선택';
                    cell.style.backgroundColor = '#ffffff';
                    cell.style.cursor = 'default';
                }
                
                row.appendChild(cell);
            }
            
            tbody.appendChild(row);
        });
        
        const clickListener = function(e) {
            const cell = e.target.closest('.slot-cell');
            if (!cell || cell.classList.contains('disabled') || !cell.classList.contains('has-counselors')) {
                return;
            }
            
            const slotKey = cell.dataset.slotKey;
            const counselors = weekSlotMap[slotKey];
            
            if (counselors && counselors.length > 0) {
                showCounselorDropdown(cell, counselors);
            }
        };
        
        tbody._clickListener = clickListener;
        tbody.addEventListener('click', clickListener);
        
        loadAvailableDates();
    }

    function loadAvailableDates() {
        weekSlotMap = {};
        let pendingRequests = 0;
        
        for (let i = 0; i < 5; i++) {
            const date = new Date(currentWeekStart);
            date.setDate(date.getDate() + i);
            if (date >= new Date(new Date().setHours(0,0,0,0))) {
                pendingRequests++;
                checkAvailableSlots(new Date(date), () => {
                    pendingRequests--;
                    if (pendingRequests === 0) {
                        updateCalendarDisplay();
                    }
                });
            }
        }
    }

    function checkAvailableSlots(date, callback) {
        const dateStr = date.toISOString().split('T')[0];
        
        fetch(`/api/counseling/schedule/available-slots?date=${dateStr}&field=${counselingField}`)
            .then(response => response.json())
            .then(slots => {
                slots.forEach(slot => {
                    const timeKey = slot.startTime;
                    const slotKey = `${dateStr}_${timeKey}`;
                    if (!weekSlotMap[slotKey]) {
                        weekSlotMap[slotKey] = [];
                    }
                    weekSlotMap[slotKey].push(slot);
                });
                if (callback) callback();
            })
            .catch(error => {
                console.error('Error:', error);
                if (callback) callback();
            });
    }

    function updateCalendarDisplay() {
        const cells = document.querySelectorAll('#scheduleBody .slot-cell:not(.disabled)');
        cells.forEach(cell => {
            const slotKey = cell.dataset.slotKey;
            const counselors = weekSlotMap[slotKey];
            
            if (counselors && counselors.length > 0) {
                cell.style.backgroundColor = '#d1ecf1';
                cell.style.color = '#0c5460';
                cell.textContent = `상담사 ${counselors.length}명`;
                cell.style.fontWeight = 'bold';
                cell.style.cursor = 'pointer';
                cell.classList.add('has-counselors');
            } else {
                cell.style.backgroundColor = '#ffffff';
                cell.style.color = '#6c757d';
                cell.textContent = '상담사 선택';
                cell.style.cursor = 'default';
                cell.classList.remove('has-counselors');
            }
        });
    }

    function showCounselorDropdown(cell, counselors) {
        document.querySelectorAll('.counselor-dropdown').forEach(d => d.remove());
        
        const dropdown = document.createElement('div');
        dropdown.className = 'counselor-dropdown';
        dropdown.style.position = 'absolute';
        dropdown.style.backgroundColor = 'white';
        dropdown.style.border = '2px solid #0c5460';
        dropdown.style.borderRadius = '4px';
        dropdown.style.boxShadow = '0 4px 12px rgba(0,0,0,0.25)';
        dropdown.style.zIndex = '10000';
        dropdown.style.minWidth = '200px';
        dropdown.style.maxHeight = '300px';
        dropdown.style.overflowY = 'auto';
        
        counselors.forEach(counselor => {
            const item = document.createElement('div');
            item.className = 'counselor-item';
            item.style.padding = '10px 15px';
            item.style.cursor = 'pointer';
            item.style.borderBottom = '1px solid #f0f0f0';
            item.textContent = counselor.counselorName;
            item.dataset.counselorData = JSON.stringify(counselor);
            item.dataset.cellDate = cell.dataset.date;
            
            item.addEventListener('mouseenter', function() {
                this.style.backgroundColor = '#f8f9fa';
            });
            
            item.addEventListener('mouseleave', function() {
                this.style.backgroundColor = 'white';
            });
            
            dropdown.appendChild(item);
        });
        
        dropdown.addEventListener('click', function(e) {
            const item = e.target.closest('.counselor-item');
            if (item) {
                const counselor = JSON.parse(item.dataset.counselorData);
                selectedDate = new Date(item.dataset.cellDate);
                selectedSlot = counselor;
                dropdown.remove();
                showReservationModal();
            }
        });
        
        const rect = cell.getBoundingClientRect();
        dropdown.style.left = (rect.left + window.scrollX) + 'px';
        dropdown.style.top = (rect.bottom + window.scrollY) + 'px';
        
        document.body.appendChild(dropdown);
        
        setTimeout(() => {
            function closeDropdown(e) {
                if (!dropdown.contains(e.target) && !cell.contains(e.target)) {
                    dropdown.remove();
                    document.removeEventListener('click', closeDropdown);
                    window.removeEventListener('scroll', onScroll, true);
                }
            }
            function onScroll() {
                dropdown.remove();
                document.removeEventListener('click', closeDropdown);
                window.removeEventListener('scroll', onScroll, true);
            }
            document.addEventListener('click', closeDropdown);
            window.addEventListener('scroll', onScroll, true);
        }, 100);
    }

    function showReservationModal() {
        const dateInput = document.getElementById('counselingDate');
        const timeSelect = document.getElementById('counselingTime');
        
        dateInput.value = selectedDate.toISOString().split('T')[0];
        dateInput.readOnly = true;
        dateInput.style.backgroundColor = '#e9ecef';
        
        timeSelect.innerHTML = `
            <option value="${selectedSlot.startTime}">${selectedSlot.startTime} - ${selectedSlot.endTime} (상담사: ${selectedSlot.counselorName})</option>
        `;
        timeSelect.disabled = true;
        timeSelect.style.backgroundColor = '#e9ecef';
        
        new bootstrap.Modal(document.getElementById('reservationModal')).show();
    }

    function submitReservation() {
        const category = document.getElementById('counselingCategory').value;
        const date = document.getElementById('counselingDate').value;
        const time = document.getElementById('counselingTime').value;
        const content = document.getElementById('counselingContent').value;
        
        if (!category || !date || !time || !content) {
            alert('모든 필드를 입력해주세요.');
            return;
        }

        const reservationData = {
            counselorId: selectedSlot.counselorId,
            counselingField: counselingField,
            subFieldId: getSubFieldId(category),
            reservationDate: date,
            startTime: time,
            endTime: getEndTime(time),
            requestContent: content
        };

        const token = document.cookie.split('; ').find(row => row.startsWith('accessToken='))?.split('=')[1];
        
        fetch('/api/counseling/reservations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reservationData)
        })
        .then(response => {
            if (response.ok) {
                alert('상담 예약이 완료되었습니다.');
                bootstrap.Modal.getInstance(document.getElementById('reservationModal')).hide();
                window.location.href = '/counseling/student/status';
            } else {
                throw new Error('예약 처리 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('예약 처리 중 오류가 발생했습니다.');
        });
    }

    function getSubFieldId(category) {
        const subFieldMap = {
            'stress': 1, 'anxiety': 2, 'depression': 3, 'relationship': 4, 'academic': 5,
            'career-exploration': 6, 'major-selection': 7, 'career-planning': 8,
            'study-method': 9, 'time-management': 10, 'exam-preparation': 11
        };
        return subFieldMap[category] || 1;
    }

    function getEndTime(startTime) {
        const [hour, minute] = startTime.split(':');
        return `${String(parseInt(hour) + 1).padStart(2, '0')}:${minute}`;
    }
});
