@echo off
cd "c:\Users\admin\Desktop\새 폴더\SCMS\student-competency-management-system\src\main\resources\static\css"

powershell -Command "(Get-Content dashboard.css) -replace '#667eea', 'var(--primary-color)' -replace '#764ba2', 'var(--primary-dark)' -replace '#5568d3', 'var(--primary-dark)' | Set-Content dashboard.css"

powershell -Command "(Get-Content noncurricular\noncurricular.css) -replace '#667eea', 'var(--primary-color)' -replace '#764ba2', 'var(--primary-dark)' | Set-Content noncurricular\noncurricular.css" 2>nul

for /r %%f in (*.css) do (
    powershell -Command "(Get-Content '%%f') -replace '#667eea', 'var(--primary-color)' -replace '#764ba2', 'var(--primary-dark)' | Set-Content '%%f'" 2>nul
)

echo Colors updated successfully!