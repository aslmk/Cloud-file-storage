$('#fileMoveModal').on('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const parentPath = button.getAttribute('data-parent-path');
    const fileName = button.getAttribute('data-file-name');

    const modal = this;
    modal.querySelector('#parentPath').value = parentPath;
    modal.querySelector('#fileName').value = fileName;
    modal.querySelector('#movedFileName').textContent = fileName;
});

$('#folderMoveModal').on('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const parentPath = button.getAttribute('data-parent-path');
    const folderName = button.getAttribute('data-folder-name');

    const modal = this;
    modal.querySelector('#parentPath').value = parentPath;
    modal.querySelector('#folderName').value = folderName;
    modal.querySelector('#movedFolderName').textContent = folderName;
});

document.addEventListener('DOMContentLoaded', () => {

    const btn = document.querySelectorAll('.move-folder-btn');

    btn.forEach(button => {
        button.addEventListener('click', async () => {
            const folderName = button.dataset.folderName;
            let parentPath = button.dataset.parentPath;

            if (parentPath === null || parentPath === undefined) {
                parentPath = '';
            }
            const fullPath = parentPath + folderName + '/';

            const res = await fetch(`/folder/move?movingPath=${encodeURIComponent(fullPath)}`);
            const html = await res.text();

            const selectElement = document.getElementById('targetFolderForFolderMove');
            selectElement.innerHTML = html;

            document.getElementById('folderName').value = folderName;
            document.getElementById('parentPath').value = parentPath;
            document.getElementById('movedFolderName').innerText = folderName;
        });
    });
});
