function createButton() {
    const button = document.createElement('button');
    button.textContent = 'Завантажити';
    button.className = 'download-button';
    button.style.display = 'none';

    const statusMessage = document.createElement('div');
    statusMessage.className = 'status-message';
    statusMessage.style.display = 'none';

    button.onclick = (event) => {
        event.stopPropagation();
        event.preventDefault();

        const url = button.dataset.url;
        statusMessage.textContent = '';

        if (url) {
            fetch('http://localhost:8080/api/download/add?url=' + encodeURIComponent(url), {
                method: 'POST',
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(responseText => {
                    console.log('URL sent to Download Manager:', responseText);
                    statusMessage.textContent = 'URL sent!';
                    statusMessage.style.color = 'green';
                    statusMessage.style.display = 'block';
                })
                .catch(error => {
                    console.error('Error sending URL to Download Manager:', error);
                    statusMessage.textContent = `Error: ${error.message}`;
                    statusMessage.style.color = 'red';
                    statusMessage.style.display = 'block';
                });
        } else {
            statusMessage.textContent = 'Input URL';
            statusMessage.style.color = 'orange';
            statusMessage.style.display = 'block';
        }
    };

    document.body.appendChild(button);
    document.body.appendChild(statusMessage);
    return { button, statusMessage };
}

function addHoverEffectToUrls() {
    const urlElements = document.querySelectorAll('a[href]');
    const { button, statusMessage } = createButton();
    let timeoutId;

    urlElements.forEach(urlElement => {
        const href = urlElement.href;
        if (href.startsWith('http://') || href.startsWith('https://')) {
            urlElement.addEventListener('mouseover', (event) => {
                clearTimeout(timeoutId);
                const rect = event.target.getBoundingClientRect();
                button.style.top = `${rect.top + window.scrollY}px`;
                button.style.left = `${rect.right + 10}px`;
                button.style.display = 'block';
                button.dataset.url = href;
                statusMessage.style.display = 'none';
            });

            urlElement.addEventListener('mouseout', () => {
                timeoutId = setTimeout(() => {
                    button.style.display = 'none';
                }, 500);
            });
        }
    });

    button.addEventListener('mouseover', () => {
        clearTimeout(timeoutId);
    });

    button.addEventListener('mouseout', () => {
        timeoutId = setTimeout(() => {
            button.style.display = 'none';
            statusMessage.style.display = 'none';
        }, 500);
    });
}

window.addEventListener('load', addHoverEffectToUrls);

function createDropZone() {
    const dropZone = document.createElement('div');
    dropZone.id = 'dropZone';
    dropZone.textContent = 'Перетягніть URL сюди для завантаження';
    dropZone.style.position = 'fixed';
    dropZone.style.bottom = '10px';
    dropZone.style.right = '10px';
    dropZone.style.width = '200px';
    dropZone.style.height = '50px';
    dropZone.style.border = '2px dashed #ccc';
    dropZone.style.borderRadius = '4px';
    dropZone.style.textAlign = 'center';
    dropZone.style.lineHeight = '50px';
    dropZone.style.backgroundColor = '#f9f9f9';
    dropZone.style.color = '#ccc';
    dropZone.style.zIndex = '1000';
    document.body.appendChild(dropZone);

    dropZone.addEventListener('dragover', (event) => {
        event.preventDefault();
        dropZone.style.borderColor = '#4CAF50';
        dropZone.style.color = '#4CAF50';
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.style.borderColor = '#ccc';
        dropZone.style.color = '#ccc';
    });

    dropZone.addEventListener('drop', (event) => {
        event.preventDefault();
        dropZone.style.borderColor = '#ccc';
        dropZone.style.color = '#ccc';

        const data = event.dataTransfer.getData('text/plain');
        if (data) {
            fetch('http://localhost:8080/api/download/add?url=' + encodeURIComponent(data), {
                method: 'POST',
            })
                .then(response => response.text())
                .then(responseText => {
                    console.log('URL sent to Download Manager:', responseText);
                })
                .catch(error => {
                    console.error('Error sending URL to Download Manager:', error);
                });
        }
    });
}

window.addEventListener('load', createDropZone);