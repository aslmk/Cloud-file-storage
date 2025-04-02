function updateFileName() {
    const fileInput = document.getElementById('file');
    const fileNameElement = document.getElementById('selectedFileName');

    if (fileInput.files.length > 0) {
        fileNameElement.textContent = "Выбран файл: " + fileInput.files[0].name;
        fileNameElement.classList.remove('d-none');
    } else {
        fileNameElement.classList.add('d-none');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('file');

    if (dropZone && fileInput) {
        dropZone.addEventListener('click', () => {
            fileInput.click();
        });

        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.classList.add('border-primary');
        });

        dropZone.addEventListener('dragleave', () => {
            dropZone.classList.remove('border-primary');
        });

        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.classList.remove('border-primary');

            if (e.dataTransfer.files.length) {
                fileInput.files = e.dataTransfer.files;
                updateFileName();
            }
        });
    }
});