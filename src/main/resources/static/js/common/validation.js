// static/js/common/validation.js
class FormValidator {
    static debounce(func, delay = CONFIG.UI.DEBOUNCE_DELAY) {
        let timeoutId;
        return (...args) => {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    }

    static validateEmail(email) {
        return CONFIG.VALIDATION.EMAIL_PATTERN.test(email);
    }

    static validatePhone(phone) {
        return CONFIG.VALIDATION.PHONE_PATTERN.test(phone);
    }

    static validatePassword(password) {
        return CONFIG.VALIDATION.PASSWORD_PATTERN.test(password);
    }

    static showFieldError(fieldId, message) {
        const field = document.getElementById(fieldId);
        const feedback = field.parentNode.querySelector('.invalid-feedback');

        field.classList.add('is-invalid');
        if (feedback) feedback.textContent = message;
    }

    static clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        field.classList.remove('is-invalid');
    }

    static setupRealTimeValidation(fieldId, validationFunc, errorMessage) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        const debouncedValidation = this.debounce((value) => {
            if (value.length === 0) {
                this.clearFieldError(fieldId);
                return;
            }

            if (validationFunc(value)) {
                this.clearFieldError(fieldId);
            } else {
                this.showFieldError(fieldId, errorMessage);
            }
        });

        field.addEventListener('input', (e) => {
            debouncedValidation(e.target.value.trim());
        });
    }
}
