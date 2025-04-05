function updateItemName() {
    const itemInput = document.getElementById('item');
    const itemNameElement = document.getElementById('selectedItemName');

    if (itemInput.files.length > 0) {
        let itemText = "";
        if (itemInput.files.length === 1) {
            itemText = "File selected: " + itemInput.files[0].name;
        } else {
            itemText = "Selected files: " + itemInput.files.length;
        }
        itemNameElement.textContent = itemText;
        itemNameElement.classList.remove('d-none');
    } else {
        itemNameElement.classList.add('d-none');
    }
}

function toggleUploadType() {
    const itemUploadRadio = document.getElementById('fileUpload');
    const itemInput = document.getElementById('item');

    if (itemUploadRadio.checked) {
        itemInput.removeAttribute('webkitdirectory');
        itemInput.removeAttribute('directory');
        itemInput.removeAttribute('multiple');
    } else {
        itemInput.setAttribute('webkitdirectory', '');
        itemInput.setAttribute('directory', '');
        itemInput.setAttribute('multiple', '');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const dropZone = document.getElementById('dropZone');
    const itemInput = document.getElementById('item');
    const fileUploadRadio = document.getElementById('fileUpload');
    const folderUploadRadio = document.getElementById('folderUpload');

    if (fileUploadRadio && folderUploadRadio) {
        fileUploadRadio.addEventListener('change', toggleUploadType);
        folderUploadRadio.addEventListener('change', toggleUploadType);
        toggleUploadType();
    }

    if (dropZone && itemInput) {
        dropZone.addEventListener('click', () => {
            itemInput.click();
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
                itemInput.files = e.dataTransfer.files;
                updateItemName();
            }
        });
    }
});