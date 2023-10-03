function getAllDevicesPromise() {
    const devices = fetch('/api/device')
        .then(response => response.json());
    return devices;
};

function getDevicePromise(id) {
    const device = fetch(`/api/device/${id}`)
         .then(response => {
            if (response.status == 404) {
                window.location.replace('/device');
            } else {
                return response.json();
            }
         });
    return device;
};

function createDevice(device) {
    fetch('/api/device', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(device)})
    .then(response => {
        handleSaveDeviceResponse(response);
    })
};

function updateDevice(device) {
    fetch(`/api/device/${device.id}`, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(device)})
        .then(response => {
            handleSaveDeviceResponse(response);
        })
};

function handleSaveDeviceResponse(response) {
    if (response.status == 200 || response.status == 201) {
        window.location.replace('/device');
    } else if (response.status == 400) {
        response.json().then(error => {handleValidationError(error)});
    } else {
        console.error('Error saving device');
    }
};


function deleteDevice(id) {
    fetch(`/api/device/${id}`, {method: 'DELETE'})
        .then(response => location.reload());
};

function getAllModelsPromise() {
    const models = fetch('/api/model')
        .then(response => response.json());
    return models;
};

/* метод для отрисовки ошибок валидации на странице. Принимает тело ответа, которое должно представлять собой ErrorDto*/
function handleValidationError(error) {
    const fields = error.fields;
    if (fields) {
        const fieldApiToHtmlIdMap = new Map([
            ['modelId', 'model-errors'],
            ['name', 'name-errors'],
            ['description', 'description-errors'],
            ['domainName', 'domain-errors'],
            ['login', 'login-errors'],
            ['password', 'password-errors']
        ]);
        fieldApiToHtmlIdMap.forEach(function(errorContainerId, fieldName) {
            const fieldMessages = fields[fieldName];
            const errorContainer = document.getElementById(errorContainerId);
            if (fieldMessages) {
                if (errorContainer) {
                    errorContainer.removeAttribute('hidden');
                    errorContainer.innerHTML = fieldMessages.join('<br/>');
                }
            } else {
                if (errorContainer) {
                    errorContainer.setAttribute('hidden', 'hidden');
                    errorContainer.innerHTML = '';
                }
            }
        });
    } else {
        console.error('Bad user request');
    }
};