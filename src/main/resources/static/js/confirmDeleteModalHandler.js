$('#confirmDeleteModal').on('show.bs.modal', function (event) {
    const button = $(event.relatedTarget);
    const itemPath = button.data('whatever');

    const cleanPath = itemPath.endsWith('/') ? itemPath.slice(0, -1) : itemPath;
    const parts = cleanPath.split('/');
    const displayName = parts[parts.length - 1];

    $('#deleteItemName').text(displayName);
    $('#deleteItemPath').val(itemPath);
});
