(function() {
    if (!window.showEditForm) {
        window.showEditForm = function() {
            document.getElementById('userInfoForm').style.display = 'block';
            event.target.style.display = 'none';
        };
        
        window.hideEditForm = function() {
            document.getElementById('userInfoForm').style.display = 'none';
            document.querySelector('button[onclick="showEditForm()"]').style.display = 'block';
        };
        
        window.getCookie = function(name) {
            const value = `; ${document.cookie}`;
            const parts = value.split(`; ${name}=`);
            if (parts.length === 2) return parts.pop().split(';').shift();
            return null;
        };
        
        window.getAuthHeaders = function() {
            const token = getCookie('accessToken');
            return token ? {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            } : {
                'Content-Type': 'application/json'
            };
        };
        
        window.submitForm = async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const phone = document.getElementById('phone').value.trim();
            
            try {
                const response = await fetch('/api/user/info', {
                    method: 'PATCH',
                    headers: getAuthHeaders(),
                    credentials: 'include',
                    body: JSON.stringify({
                        email: email || null,
                        phone: phone || null
                    })
                });
                
                if (response.ok) {
                    alert('정보가 성공적으로 수정되었습니다.');
                    hideEditForm();
                    location.reload();
                } else {
                    alert('정보 수정 중 오류가 발생했습니다.');
                }
            } catch (error) {
                console.error('정보 수정 실패:', error);
                alert('정보 수정 중 오류가 발생했습니다.');
            }
        };
    }
})();
