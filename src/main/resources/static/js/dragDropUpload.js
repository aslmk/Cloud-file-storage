function updateItemName(inputId, labelId) {
    const input = document.getElementById(inputId);
    const label = document.getElementById(labelId);

    if (input && label) {
        if (input.files.length === 0) {
            label.classList.add("d-none");
            return;
        }

        label.textContent = input.files.length === 1
            ? "File selected: " + input.files[0].name
            : "Selected files: " + input.files.length;
        label.classList.remove("d-none");
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const configs = [
        {
            dropZoneId: "dropZoneFile",
            inputId: "fileItem",
            labelId: "selectedFileName"
        },
        {
            dropZoneId: "dropZoneFolder",
            inputId: "folderItem",
            labelId: "selectedFolderName"
        }
    ];

    configs.forEach(({ dropZoneId, inputId, labelId }) => {
        const dropZone = document.getElementById(dropZoneId);
        const input = document.getElementById(inputId);

        if (!dropZone || !input) return;

        dropZone.addEventListener("click", () => {
            input.click();
        });

        dropZone.addEventListener("dragover", (e) => {
            e.preventDefault();
            dropZone.classList.add("border-primary");
        });

        dropZone.addEventListener("dragleave", () => {
            dropZone.classList.remove("border-primary");
        });

        dropZone.addEventListener("drop", (e) => {
            e.preventDefault();
            dropZone.classList.remove("border-primary");

            if (e.dataTransfer.files.length > 0) {
                input.files = e.dataTransfer.files;
                updateItemName(inputId, labelId);
            }
        });
    });
});