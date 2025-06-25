function getParameterByName(name) {
    const url = window.location.href;
    name = name.replace(/[$$$$]/g, '\\$&');
    const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');
    const results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return results[2];
}

document.addEventListener('DOMContentLoaded', function() {
    const pathValue = getParameterByName('path');
    document.getElementById('path').value = pathValue;
    document.getElementById('renamePath').value = pathValue;
});