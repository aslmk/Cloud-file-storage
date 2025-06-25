function toggleForms() {
    const fileForm = document.getElementById("fileUploadForm");
    const folderForm = document.getElementById("folderUploadForm");

    if (document.getElementById("fileUpload").checked) {
        fileForm.classList.remove("d-none");
        folderForm.classList.add("d-none");
    } else {
        fileForm.classList.add("d-none");
        folderForm.classList.remove("d-none");
    }
}