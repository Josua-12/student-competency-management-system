document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('writtenEditingForm');
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        const formData = new FormData();
        formData.append('editingType', document.getElementById('editingType').value);
        formData.append('companyName', document.getElementById('companyName').value);
        formData.append('jobPosition', document.getElementById('jobPosition').value);
        formData.append('recruitmentStage', document.getElementById('recruitmentStage').value);
        formData.append('recruitmentType', document.getElementById('recruitmentType').value);
        formData.append('requestContent', document.getElementById('requestContent').value);
        
        const fileInput = document.getElementById('attachmentFile');
        if (fileInput.files.length > 0) {
            formData.append('file', fileInput.files[0]);
        }
        
        fetch('/api/counseling/written-editing', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            alert('신청이 완료되었습니다.');
            window.location.href = '/counseling/student/state';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('신청 중 오류가 발생했습니다.');
        });
    });
    
    function validateForm() {
        const privacyAgree = document.getElementById('privacyAgree').checked;
        const sensitiveAgree = document.getElementById('sensitiveAgree').checked;
        const fileInput = document.getElementById('attachmentFile');
        
        if (!privacyAgree || !sensitiveAgree) {
            alert('개인정보 활용에 동의해주세요.');
            return false;
        }
        
        if (fileInput.files.length === 0) {
            alert('첨부파일을 선택해주세요.');
            return false;
        }
        
        return true;
    }
});
