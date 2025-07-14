$('#confirmFileDeleteModal').on('show.bs.modal', function (event) {
    const button = $(event.relatedTarget);
    const filePath = button.data('whatever');

    const parts = filePath.split('/');
    const displayName = parts[parts.length - 1];

    $('#deleteFileName').text(displayName);
    $('#deleteFilePath').val(filePath);
});

$('#confirmFolderDeleteModal').on('show.bs.modal', function (event) {
    const button = $(event.relatedTarget);
    const folderPath = button.data('whatever');

    const cleanPath = folderPath.slice(0, -1);
    const parts = cleanPath.split('/');
    const displayName = parts[parts.length - 1];

    $('#deleteFolderName').text(displayName);
    $('#deleteFolderPath').val(folderPath);
});
