const fs = require('fs');
const path = require('path');

const colorMap = {
    // 파란색 계열 -> #094081
    '#007bff': '#094081', '#0056b3': '#094081', '#004085': '#094081',
    '#667eea': '#094081', '#764ba2': '#094081', '#5568d3': '#094081',
    '#6f42c1': '#094081', '#5a32a3': '#094081', '#495057': '#094081',
    '#343a40': '#094081', '#17a2b8': '#094081', '#138496': '#094081',
    
    // 노란색 계열 -> #EFCD69
    '#ffc107': '#EFCD69', '#e0a800': '#EFCD69', '#d39e00': '#EFCD69',
    '#ffeb3b': '#EFCD69', '#fdd835': '#EFCD69', '#f9a825': '#EFCD69',
    '#28a745': '#EFCD69', '#218838': '#EFCD69', '#1e7e34': '#EFCD69',
    
    // 하늘색 계열 -> #04A5E9
    '#20c997': '#04A5E9', '#1ea080': '#04A5E9', '#17a2b8': '#04A5E9',
    '#8b9fff': '#04A5E9', '#87ceeb': '#04A5E9', '#00bcd4': '#04A5E9',
    
    // 흰색 계열 -> #D7D4D5
    '#ffffff': '#D7D4D5', '#f8f9fa': '#D7D4D5', '#e9ecef': '#D7D4D5',
    '#dee2e6': '#D7D4D5', '#ced4da': '#D7D4D5', '#adb5bd': '#D7D4D5'
};

function replaceColorsInFile(filePath) {
    try {
        let content = fs.readFileSync(filePath, 'utf8');
        let changed = false;
        
        for (const [oldColor, newColor] of Object.entries(colorMap)) {
            const regex = new RegExp(oldColor.replace('#', '\\#'), 'gi');
            if (content.match(regex)) {
                content = content.replace(regex, newColor);
                changed = true;
            }
        }
        
        if (changed) {
            fs.writeFileSync(filePath, content, 'utf8');
            console.log(`Updated: ${filePath}`);
        }
    } catch (error) {
        console.error(`Error processing ${filePath}:`, error.message);
    }
}

function walkDirectory(dir, extensions) {
    const files = fs.readdirSync(dir);
    
    for (const file of files) {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);
        
        if (stat.isDirectory()) {
            walkDirectory(fullPath, extensions);
        } else if (extensions.some(ext => file.endsWith(ext))) {
            replaceColorsInFile(fullPath);
        }
    }
}

// CSS와 JS 파일 처리
const staticDir = 'c:\\Users\\admin\\Desktop\\새 폴더\\SCMS\\student-competency-management-system\\src\\main\\resources\\static';
walkDirectory(staticDir, ['.css', '.js']);

console.log('Color replacement completed!');