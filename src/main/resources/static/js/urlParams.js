function getParameterByName(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}


document.addEventListener('DOMContentLoaded', function() {
    const pathValue = getParameterByName('path');

    document.getElementById('path-file').value = pathValue;
    document.getElementById('path-folder').value = pathValue;
    document.getElementById('renamePath').value = pathValue;
});