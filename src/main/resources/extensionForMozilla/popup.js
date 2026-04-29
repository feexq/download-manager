document.getElementById('sendButton').addEventListener('click', () => {
    const url = document.getElementById('urlInput').value;
    const statusMessage = document.getElementById('statusMessage');

    statusMessage.textContent = '';

    if (url) {
        fetch('http://localhost:8080/api/download/add?url=' + encodeURIComponent(url), {
            method: 'POST',
        })
            .then(response => response.text())
            .then(responseText => {
                console.log('URL sent to Download Manager:', responseText);
                statusMessage.textContent = 'URL sent!';
                statusMessage.style.color = 'green';
            })
            .catch(error => {
                console.error('Error sending URL to Download Manager:', error);
                statusMessage.textContent = 'Error';
                statusMessage.style.color = 'red';
            });
    } else {
        statusMessage.textContent = 'Input URL';
        statusMessage.style.color = 'orange';
    }
});