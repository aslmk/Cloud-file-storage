function updateFileName() {
    const fileInput = document.getElementById('file');
    const fileNameElement = document.getElementById('selectedFileName');

    if (fileInput.files.length > 0) {
        let fileText = "";
        if (fileInput.files.length === 1) {
            fileText = "File selected: " + fileInput.files[0].name;
        } else {
            fileText = "Selected files: " + fileInput.files.length;
        }
        fileNameElement.textContent = fileText;
        fileNameElement.classList.remove('d-none');
    } else {
        fileNameElement.classList.add('d-none');
    }
}

function toggleUploadType() {
    const fileUploadRadio = document.getElementById('fileUpload');
    const folderUploadRadio = document.getElementById('folderUpload');
    const fileInput = document.getElementById('file');

    if (fileUploadRadio.checked) {
        fileInput.removeAttribute('webkitdirectory');
        fileInput.removeAttribute('directory');
        fileInput.removeAttribute('multiple');
    } else {
        fileInput.setAttribute('webkitdirectory', '');
        fileInput.setAttribute('directory', '');
        fileInput.setAttribute('multiple', '');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('file');
    const fileUploadRadio = document.getElementById('fileUpload');
    const folderUploadRadio = document.getElementById('folderUpload');

    if (fileUploadRadio && folderUploadRadio) {
        fileUploadRadio.addEventListener('change', toggleUploadType);
        folderUploadRadio.addEventListener('change', toggleUploadType);
        toggleUploadType();
    }

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