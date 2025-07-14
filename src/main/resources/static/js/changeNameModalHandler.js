$('#fileNameChangeModal').on('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const parentPath = button.getAttribute('data-parent-path');
    const fileName = button.getAttribute('data-item-name');

    const modal = this;
    modal.querySelector('#parentPath').value = parentPath;
    modal.querySelector('#currentFileName').value = fileName;
    modal.querySelector('#newFileNameInput').value = fileName;
});

$('#folderNameChangeModal').on('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const parentPath = button.getAttribute('data-parent-path');
    const folderName = button.getAttribute('data-item-name');

    const modal = this;
    modal.querySelector('#parentPath').value = parentPath;
    modal.querySelector('#currentFolderName').value = folderName;
    modal.querySelector('#newFolderNameInput').value = folderName;
});