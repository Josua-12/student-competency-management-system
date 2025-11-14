document.querySelectorAll('input[name="counselingType"]').forEach(radio => {
    radio.addEventListener('change', function () {
        const goTo = {
            state: "/counseling/student/status",
            GeneralCnsl: "/counseling/student",
            psycho: "/counseling/student/psycho",
            career: "/counseling/student/career",
            job: "/counseling/student/job",
            learning: "/counseling/student/learning"
        };
        window.location.href = goTo[this.value];
    });
});