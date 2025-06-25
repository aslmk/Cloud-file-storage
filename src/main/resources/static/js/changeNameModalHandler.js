$('#changeNameModal').on('show.bs.modal', function (event) {
    const itemName = $(event.relatedTarget).data("whatever");

    const cleanPath = itemName.endsWith('/') ? itemName.slice(0, -1) : itemName;

    const parts = cleanPath.split('/');
    const displayName = parts[parts.length - 1];

    $('#itemNameInput').val(displayName);
    $('#originalItemName').val(itemName);
});