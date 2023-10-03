function renderDeviceEditionFormData() {
    const deviceId = document.getElementById('id-input').value;
    if (deviceId) {
        getDevicePromise(deviceId).then(device => {
        renderModelSelectData(device);
        document.getElementById('new-device-name').value = device.name;
        document.getElementById('new-device-description').value = device.description;
        document.getElementById('new-device-domain').value = device.domainName;
        });
    } else {
        renderModelSelectData();
    }
};

function renderModelSelectData(device) {
    const modelSelect = document.getElementById('new-device-model');
    getAllModelsPromise().then(models => {
        models.forEach(model => {
            const option = document.createElement('option');
            option.value = model.id;
            option.textContent = model.vendor.name + ' ' + model.name;
            if (device && device.deviceInfo.modelId == model.id) {
                option.setAttribute('selected', 'selected');
            }
            modelSelect.appendChild(option);
        });
    });
};

function saveDevice() {
    var device = new Object();
    device.id = document.getElementById('id-input').value;
    var selectModelElem = document.getElementById('new-device-model');
    device.modelId = selectModelElem.options[selectModelElem.selectedIndex].value;
    device.name = document.getElementById('new-device-name').value;
    device.description = document.getElementById('new-device-description').value;
    device.domainName = document.getElementById('new-device-domain').value;
    device.login = document.getElementById('new-device-login').value;
    device.password = document.getElementById('new-device-password').value;
    if (device.id) {
        updateDevice(device);
    } else {
        createDevice(device);
    }
};