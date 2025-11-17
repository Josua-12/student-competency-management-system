document.addEventListener('DOMContentLoaded', function() {
    let currentPage = 0;
    let currentReservation = null;

    // 초기 데이터 로드
    loadReservations();

    // 검색 버튼 이벤트
    document.querySelector('.btn-secondary').addEventListener('click', function() {
        currentPage = 0;
        loadReservations();
    });

    // 상담 예약 목록 로드
    function loadReservations() {
        const searchCondition = getSearchCondition();
        
        fetch(`/api/counseling/reservations?${new URLSearchParams(searchCondition)}`)
            .then(response => response.json())
            .then(data => {
                renderReservationTable(data.content);
                renderPagination(data);
                updateTotalCount(data.totalElements);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('데이터를 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 검색 조건 수집
    function getSearchCondition() {
        const dateTypeEl = document.querySelector('input[name="dateTypeRadio"]:checked');
        const dateType = dateTypeEl ? dateTypeEl.value : 'reservation';
        const dateInputs = document.querySelectorAll('input[type="text"]');
        const startDate = dateInputs[0] ? dateInputs[0].value : '';
        const endDate = dateInputs[1] ? dateInputs[1].value : '';
        const statusEl = document.querySelector('select');
        const status = statusEl ? statusEl.value : '전체';

        const params = {
            page: currentPage,
            size: 10,
            dateType: dateType
        };

        if (startDate) params.startDate = startDate;
        if (endDate) params.endDate = endDate;
        if (status !== '전체') {
            const statusMap = {
                '대기중': 'PENDING',
                '승인됨': 'CONFIRMED', 
                '완료됨': 'COMPLETED',
                '취소됨': 'CANCELLED'
            };
            params.status = statusMap[status];
        }

        return params;
    }

    // 테이블 렌더링
    function renderReservationTable(reservations) {
        const tbody = document.querySelector('tbody');
        tbody.innerHTML = '';

        reservations.forEach(reservation => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${getFieldDisplayName(reservation.counselingField)}</td>
                <td>${formatDateTime(reservation.createdAt)}</td>
                <td>${reservation.confirmedDate ? formatDate(reservation.confirmedDate) + ' ' + formatTime(reservation.confirmedStartTime) : '-'}</td>
                <td>${reservation.counselorName || '-'}</td>
                <td><span class="badge ${getStatusBadgeClass(reservation.status)}">${getStatusDisplayName(reservation.status)}</span></td>
                <td>${reservation.requestContent || '-'}</td>
                <td>
                    <button class="btn btn-outline-primary btn-sm me-1" onclick="showDetailModal(${reservation.id})">상세</button>
                    ${getActionButton(reservation)}
                </td>
            `;
            tbody.appendChild(row);
        });
    }

    // 상태별 액션 버튼
    function getActionButton(reservation) {
        if (reservation.status === 'PENDING' || reservation.status === 'CONFIRMED') {
            return `<button class="btn btn-outline-danger btn-sm" onclick="showCancelModal(${reservation.id})">취소</button>`;
        } else if (reservation.status === 'COMPLETED') {
            if (reservation.hasSatisfaction) {
                return `<button class="btn btn-outline-info btn-sm" onclick="showSatisfactionResult(${reservation.id})">만족도 조회</button>`;
            } else {
                return `<button class="btn btn-outline-success btn-sm" onclick="showSatisfactionModal(${reservation.id})">만족도 작성</button>`;
            }
        }
        return '';
    }

    // 상세 모달 표시
    window.showDetailModal = function(reservationId) {
        fetch(`/api/counseling/reservations/${reservationId}`)
            .then(response => response.json())
            .then(reservation => {
                document.getElementById('detailReservationId').textContent = `CNSL-${reservation.id}`;
                document.getElementById('detailField').textContent = getFieldDisplayName(reservation.counselingField);
                document.getElementById('detailCategory').textContent = reservation.subFieldName || '-';
                document.getElementById('detailRequestedDateTime').textContent = formatDateTime(reservation.reservationDate + 'T' + reservation.startTime);
                document.getElementById('detailConfirmedDateTime').textContent = 
                    reservation.confirmedDate ? formatDateTime(reservation.confirmedDate + 'T' + reservation.confirmedStartTime) : '-';
                document.getElementById('detailCounselor').textContent = reservation.counselorName || '-';
                document.getElementById('detailStatus').textContent = getStatusDisplayName(reservation.status);
                document.getElementById('detailStatus').className = `badge ${getStatusBadgeClass(reservation.status)}`;
                document.getElementById('detailContent').textContent = reservation.requestContent || '-';
                
                if (reservation.memo) {
                    document.getElementById('detailMemoSection').style.display = 'block';
                    document.getElementById('detailMemo').textContent = reservation.memo;
                } else {
                    document.getElementById('detailMemoSection').style.display = 'none';
                }
                
                if (reservation.status === 'REJECTED' && reservation.rejectReason) {
                    document.getElementById('detailRejectReasonSection').style.display = 'block';
                    document.getElementById('detailRejectReason').textContent = reservation.rejectReason;
                } else {
                    document.getElementById('detailRejectReasonSection').style.display = 'none';
                }
                
                if (reservation.counselingField === 'EMPLOYMENT') {
                    loadAttachmentsForDetail(reservationId);
                } else {
                    document.getElementById('detailAttachmentsSection').style.display = 'none';
                }

                new bootstrap.Modal(document.getElementById('detailModal')).show();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('상세 정보를 불러오는 중 오류가 발생했습니다.');
            });
    };

    // 취소 모달 표시
    window.showCancelModal = function(reservationId) {
        currentReservation = reservationId;
        document.getElementById('cancelReason').value = '';
        new bootstrap.Modal(document.getElementById('cancelModal')).show();
    };

    // 만족도 작성 모달 표시
    window.showSatisfactionModal = function(reservationId) {
        currentReservation = reservationId;
        loadSatisfactionSurvey(reservationId, false);
    };

    // 만족도 조회 모달 표시
    window.showSatisfactionResult = function(reservationId) {
        currentReservation = reservationId;
        loadSatisfactionResult(reservationId);
    };

    // 만족도 설문 로드
    function loadSatisfactionSurvey(reservationId, isEdit) {
        fetch(`/api/counseling/satisfaction/survey/${reservationId}`)
            .then(response => response.json())
            .then(survey => {
                if (isEdit) {
                    fetch(`/api/counseling/satisfaction/result/${reservationId}`)
                        .then(res => res.json())
                        .then(result => {
                            renderSatisfactionForm(survey, result);
                            document.getElementById('submitSatisfaction').textContent = '만족도 수정';
                            document.getElementById('submitSatisfaction').dataset.satisfactionId = result.satisfactionId;
                            new bootstrap.Modal(document.getElementById('satisfactionModal')).show();
                        });
                } else {
                    renderSatisfactionForm(survey);
                    document.getElementById('submitSatisfaction').textContent = '만족도 제출';
                    delete document.getElementById('submitSatisfaction').dataset.satisfactionId;
                    new bootstrap.Modal(document.getElementById('satisfactionModal')).show();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('만족도 설문을 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 만족도 결과 조회
    function loadSatisfactionResult(reservationId) {
        fetch(`/api/counseling/satisfaction/result/${reservationId}`)
            .then(response => response.json())
            .then(result => {
                renderSatisfactionResult(result);
                new bootstrap.Modal(document.getElementById('satisfactionModal')).show();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('만족도 결과를 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 만족도 폼 렌더링
    function renderSatisfactionForm(survey, existingResult) {
        const form = document.getElementById('satisfactionForm');
        form.innerHTML = '';
        
        survey.questions.forEach((question, index) => {
            const questionDiv = document.createElement('div');
            questionDiv.className = 'mb-4';
            
            const label = document.createElement('label');
            label.className = 'form-label';
            label.innerHTML = `<strong>${index + 1}. ${question.questionText}${question.isRequired ? ' <span class="text-danger">*</span>' : ''}</strong>`;
            questionDiv.appendChild(label);
            
            const existingAnswer = existingResult?.answers.find(a => a.questionId === question.questionId);
            
            if (question.questionType === 'RATING') {
                questionDiv.appendChild(createRatingInput(question, existingAnswer?.ratingValue));
            } else if (question.questionType === 'TEXT') {
                questionDiv.appendChild(createTextInput(question, existingAnswer?.answerText));
            } else if (question.questionType === 'MULTIPLE_CHOICE') {
                questionDiv.appendChild(createMultipleChoiceInput(question, existingAnswer?.selectedOptionId));
            }
            
            form.appendChild(questionDiv);
        });
    }

    // 만족도 결과 표시
    function renderSatisfactionResult(result) {
        const form = document.getElementById('satisfactionForm');
        form.innerHTML = '';
        
        result.answers.forEach((answer, index) => {
            const answerDiv = document.createElement('div');
            answerDiv.className = 'mb-4';
            
            const label = document.createElement('label');
            label.className = 'form-label';
            label.innerHTML = `<strong>${index + 1}. ${answer.questionText}</strong>`;
            answerDiv.appendChild(label);
            
            const valueDiv = document.createElement('div');
            valueDiv.className = 'p-3 bg-light rounded';
            
            if (answer.questionType === 'RATING') {
                valueDiv.textContent = `${answer.ratingValue}점`;
            } else if (answer.questionType === 'TEXT') {
                valueDiv.textContent = answer.answerText || '-';
            } else if (answer.questionType === 'MULTIPLE_CHOICE') {
                valueDiv.textContent = answer.selectedOptionText || '-';
            }
            
            answerDiv.appendChild(valueDiv);
            form.appendChild(answerDiv);
        });
        
        const editBtn = document.createElement('button');
        editBtn.type = 'button';
        editBtn.className = 'btn btn-warning mt-3';
        editBtn.textContent = '수정';
        editBtn.onclick = () => {
            bootstrap.Modal.getInstance(document.getElementById('satisfactionModal')).hide();
            loadSatisfactionSurvey(result.reservationId, true);
        };
        form.appendChild(editBtn);
        
        document.getElementById('submitSatisfaction').style.display = 'none';
    }

    // 평점 입력 생성
    function createRatingInput(question, defaultValue) {
        const container = document.createElement('div');
        container.className = 'd-flex justify-content-between align-items-center';
        
        const btnGroup = document.createElement('div');
        btnGroup.className = 'btn-group';
        btnGroup.setAttribute('role', 'group');
        
        for (let i = 1; i <= 5; i++) {
            const input = document.createElement('input');
            input.type = 'radio';
            input.className = 'btn-check';
            input.name = `question_${question.questionId}`;
            input.id = `q${question.questionId}_${i}`;
            input.value = i;
            input.dataset.questionId = question.questionId;
            input.dataset.type = 'rating';
            if (question.isRequired) input.required = true;
            if (defaultValue && defaultValue === i) input.checked = true;
            
            const label = document.createElement('label');
            label.className = 'btn btn-outline-primary';
            label.setAttribute('for', `q${question.questionId}_${i}`);
            label.textContent = i;
            
            btnGroup.appendChild(input);
            btnGroup.appendChild(label);
        }
        
        container.appendChild(btnGroup);
        return container;
    }

    // 텍스트형 입력 생성
    function createTextInput(question, defaultValue) {
        const textarea = document.createElement('textarea');
        textarea.className = 'form-control';
        textarea.name = `question_${question.questionId}`;
        textarea.rows = 4;
        textarea.dataset.questionId = question.questionId;
        textarea.dataset.type = 'text';
        if (question.isRequired) textarea.required = true;
        if (defaultValue) textarea.value = defaultValue;
        return textarea;
    }

    // 객관식 입력 생성
    function createMultipleChoiceInput(question, defaultValue) {
        const container = document.createElement('div');
        
        question.options.forEach(option => {
            const div = document.createElement('div');
            div.className = 'form-check';
            
            const input = document.createElement('input');
            input.className = 'form-check-input';
            input.type = 'radio';
            input.name = `question_${question.questionId}`;
            input.id = `opt${option.optionId}`;
            input.value = option.optionId;
            input.dataset.questionId = question.questionId;
            input.dataset.type = 'option';
            if (question.isRequired) input.required = true;
            if (defaultValue && defaultValue === option.optionId) input.checked = true;
            
            const label = document.createElement('label');
            label.className = 'form-check-label';
            label.setAttribute('for', `opt${option.optionId}`);
            label.textContent = option.optionText;
            
            div.appendChild(input);
            div.appendChild(label);
            container.appendChild(div);
        });
        
        return container;
    }

    // 취소 확정 버튼 이벤트
    document.querySelector('#cancelModal .btn-danger').addEventListener('click', function() {
        const cancelReason = document.getElementById('cancelReason').value.trim();
        if (!cancelReason) {
            alert('취소 사유를 입력해주세요.');
            return;
        }

        fetch(`/api/counseling/reservations/${currentReservation}/cancel`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ cancelReason: cancelReason })
        })
        .then(response => {
            if (response.ok) {
                alert('상담 예약을 취소했습니다');
                bootstrap.Modal.getInstance(document.getElementById('cancelModal')).hide();
                loadReservations();
            } else {
                throw new Error('취소 처리 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('취소 처리 중 오류가 발생했습니다.');
        });
    });

    // 만족도 제출 버튼 이벤트
    document.getElementById('submitSatisfaction').addEventListener('click', function() {
        const form = document.getElementById('satisfactionForm');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const answers = [];
        const inputs = form.querySelectorAll('input[data-question-id], textarea[data-question-id]');
        
        inputs.forEach(input => {
            if (input.type === 'radio' && !input.checked) return;
            
            const answer = {
                questionId: parseInt(input.dataset.questionId)
            };
            
            if (input.dataset.type === 'rating') {
                answer.ratingValue = parseInt(input.value);
            } else if (input.dataset.type === 'text') {
                answer.answerText = input.value;
            } else if (input.dataset.type === 'option') {
                answer.selectedOptionId = parseInt(input.value);
            }
            
            answers.push(answer);
        });

        const submitData = {
            reservationId: currentReservation,
            answers: answers
        };

        const satisfactionId = this.dataset.satisfactionId;
        const url = satisfactionId ? `/api/counseling/satisfaction/${satisfactionId}` : '/api/counseling/satisfaction';
        const method = satisfactionId ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(submitData)
        })
        .then(response => {
            if (response.ok) {
                alert(satisfactionId ? '만족도를 수정했습니다' : '만족도 조사가 제출되었습니다');
                bootstrap.Modal.getInstance(document.getElementById('satisfactionModal')).hide();
                loadReservations();
            } else {
                throw new Error('만족도 제출 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('만족도 제출 중 오류가 발생했습니다.');
        });
    });

    // 모달 닫힐 때 제출 버튼 복원
    document.getElementById('satisfactionModal').addEventListener('hidden.bs.modal', function() {
        document.getElementById('submitSatisfaction').style.display = 'inline-block';
    });

    // 페이지네이션 렌더링
    function renderPagination(pageData) {
        const pagination = document.querySelector('.pagination');
        pagination.innerHTML = '';

        // 이전 버튼
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${pageData.number - 1})">이전</a>`;
        pagination.appendChild(prevLi);

        // 페이지 번호들
        const startPage = Math.max(0, pageData.number - 2);
        const endPage = Math.min(pageData.totalPages - 1, pageData.number + 2);

        for (let i = startPage; i <= endPage; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === pageData.number ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>`;
            pagination.appendChild(li);
        }

        // 다음 버튼
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${pageData.number + 1})">다음</a>`;
        pagination.appendChild(nextLi);
    }

    // 페이지 변경
    window.changePage = function(page) {
        if (page >= 0) {
            currentPage = page;
            loadReservations();
        }
    };

    // 총개수 업데이트
    function updateTotalCount(total) {
        document.querySelector('.total_count strong').textContent = total;
    }

    // 유틸리티 함수들
    function getFieldDisplayName(field) {
        const fieldMap = {
            'ACADEMIC': '학업 상담',
            'CAREER': '진로 상담', 
            'PSYCHOLOGICAL': '심리 상담',
            'EMPLOYMENT': '취업 상담',
            'JOB': '취업 상담'
        };
        return fieldMap[field] || field;
    }

    function getStatusDisplayName(status) {
        const statusMap = {
            'PENDING': '대기중',
            'CONFIRMED': '승인됨',
            'COMPLETED': '완료됨',
            'CANCELLED': '취소됨',
            'REJECTED': '거절됨'
        };
        return statusMap[status] || status;
    }

    function getStatusBadgeClass(status) {
        const classMap = {
            'PENDING': 'bg-warning',
            'CONFIRMED': 'bg-primary',
            'COMPLETED': 'bg-success',
            'CANCELLED': 'bg-danger',
            'REJECTED': 'bg-secondary'
        };
        return classMap[status] || 'bg-secondary';
    }

    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return '-';
        const date = new Date(dateTimeStr);
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return date.toLocaleDateString('ko-KR');
    }

    function formatTime(timeStr) {
        if (!timeStr) return '-';
        return timeStr.substring(0, 5);
    }
    
    function loadAttachmentsForDetail(reservationId) {
        fetch(`/api/counseling/reservations/${reservationId}/attachments`)
            .then(response => response.json())
            .then(attachments => {
                const section = document.getElementById('detailAttachmentsSection');
                const container = document.getElementById('detailAttachments');
                
                if (attachments.length === 0) {
                    section.style.display = 'none';
                    return;
                }
                
                section.style.display = 'block';
                container.innerHTML = '';
                
                const studentAttachments = attachments.filter(att => 
                    att.attachmentType === 'RESUME' || att.attachmentType === 'COVER_LETTER'
                );
                const counselorAttachments = attachments.filter(att => 
                    att.attachmentType === 'DOCUMENT'
                );
                
                if (studentAttachments.length > 0) {
                    const studentLabel = document.createElement('div');
                    studentLabel.className = 'fw-bold mb-2';
                    studentLabel.textContent = '학생 첨부파일:';
                    container.appendChild(studentLabel);
                    
                    studentAttachments.forEach(att => {
                        const typeDisplay = att.attachmentType === 'RESUME' ? '이력서' : '자기소개서';
                        const fileDiv = document.createElement('div');
                        fileDiv.className = 'mb-2';
                        const link = document.createElement('a');
                        link.href = `/api/counseling/reservations/attachments/${att.id}/download`;
                        link.download = att.originalName;
                        link.className = 'btn btn-sm btn-outline-secondary';
                        link.innerHTML = `<i class="bi bi-download"></i> ${typeDisplay}: ${att.originalName}`;
                        fileDiv.appendChild(link);
                        container.appendChild(fileDiv);
                    });
                }
                
                if (counselorAttachments.length > 0) {
                    const counselorLabel = document.createElement('div');
                    counselorLabel.className = 'fw-bold mb-2 mt-3';
                    counselorLabel.textContent = '상담사 첨부파일:';
                    container.appendChild(counselorLabel);
                    
                    counselorAttachments.forEach(att => {
                        const fileDiv = document.createElement('div');
                        fileDiv.className = 'mb-2';
                        const link = document.createElement('a');
                        link.href = `/api/counseling/reservations/attachments/${att.id}/download`;
                        link.download = att.originalName;
                        link.className = 'btn btn-sm btn-outline-primary';
                        link.innerHTML = `<i class="bi bi-download"></i> ${att.originalName}`;
                        fileDiv.appendChild(link);
                        container.appendChild(fileDiv);
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
});