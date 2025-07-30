$('#fileMoveModal').on('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const parentPath = button.getAttribute('data-parent-path');
    const fileName = button.getAttribute('data-file-name');

    const modal = this;
    modal.querySelector('#parentPath').value = parentPath;
    modal.querySelector('#fileName').value = fileName;
    modal.querySelector('#movedFileName').textContent = fileName;
});