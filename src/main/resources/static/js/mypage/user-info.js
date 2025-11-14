// static/js/mypage/user-info.js
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('userInfoForm');
    const updateBtn = document.getElementById('updateBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    
    let originalData = {};
    
    loadUserInfo();
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        updateUserInfo();
    });
    
    cancelBtn.addEventListener('click', function() {
        if (confirm(CONFIG.MESSAGES.SUCCESS.CANCEL_CONFIRM)) {
            loadUserInfo();
        }
    });

    async function loadUserInfo() {
        try {
            const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO);
            const data = await response.json();
            originalData = data;

            document.getElementById('name').value = data.name || '';
            document.getElementById('email').value = data.email || '';
            document.getElementById('phone').value = data.phone || '';
            document.getElementById('department').value = data.department || '';
            document.getElementById('grade').value = data.grade || '';
        } catch (error) {
            ErrorHandler.showError(error);
        }
    }

    async function updateUserInfo() {
        clearErrors();

        const formData = {
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value
        };

        try {
            const response = await ApiUtils.request(CONFIG.API.ENDPOINTS.USER_INFO, {
                method: 'PATCH',
                body: JSON.stringify(formData)
            });

            ErrorHandler.showSuccess(CONFIG.MESSAGES.SUCCESS.USER_UPDATE);
            loadUserInfo();
        } catch (error) {
            ErrorHandler.showError(error);
        }
    }
    
    function clearErrors() {
        document.querySelectorAll('.error-message').forEach(element => {
            element.textContent = '';
        });
    }
});
