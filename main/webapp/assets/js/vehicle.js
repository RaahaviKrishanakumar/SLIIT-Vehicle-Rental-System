document.addEventListener('DOMContentLoaded', function() {
    // Show/hide vehicle type specific fields
    const typeSelect = document.querySelector('select[name="type"]');
    if (typeSelect) {
        typeSelect.addEventListener('change', function() {
            document.querySelectorAll('.vehicle-type-fields').forEach(el => {
                el.style.display = 'none';
            });

            const selectedFields = document.getElementById(this.value.toLowerCase() + 'Fields');
            if (selectedFields) selectedFields.style.display = 'block';
        });
    }
});