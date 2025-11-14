document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('userInfoForm');
    const updateBtn = document.getElementById('updateBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    
    let originalData = {};
    
    // 사용자 정보 로드
    loadUserInfo();
    
    // 폼 제출 이벤트
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        updateUserInfo();
    });
    
    // 취소 버튼
    cancelBtn.addEventListener('click', function() {
        if (confirm('변경사항을 취소하시겠습니까?')) {
            loadUserInfo();
        }
    });
    
    async function loadUserInfo() {
        try {
            const response = await fetch('/api/user/info', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                originalData = data;
                
                document.getElementById('name').value = data.name || '';
                document.getElementById('email').value = data.email || '';
                document.getElementById('phone').value = data.phone || '';
                document.getElementById('department').value = data.department || '';
                document.getElementById('grade').value = data.grade || '';
            }
        } catch (error) {
            console.error('사용자 정보 로드 실패:', error);
            alert('사용자 정보를 불러오는데 실패했습니다.');
        }
    }
    
    async function updateUserInfo() {
        clearErrors();
        
        const formData = {
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value
        };
        
        try {
            const response = await fetch('/api/user/info', {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: JSON.stringify(formData)
            });
            
            if (response.ok) {
                alert('사용자 정보가 성공적으로 수정되었습니다.');
                loadUserInfo();
            } else {
                const errorData = await response.json();
                handleValidationErrors(errorData);
            }
        } catch (error) {
            console.error('사용자 정보 수정 실패:', error);
            alert('사용자 정보 수정에 실패했습니다.');
        }
    }
    
    function handleValidationErrors(errorData) {
        if (errorData.fieldErrors) {
            errorData.fieldErrors.forEach(error => {
                const errorElement = document.getElementById(error.field + 'Error');
                if (errorElement) {
                    errorElement.textContent = error.message;
                }
            });
        }
    }
    
    function clearErrors() {
        document.querySelectorAll('.error-message').forEach(element => {
            element.textContent = '';
        });
    }
});