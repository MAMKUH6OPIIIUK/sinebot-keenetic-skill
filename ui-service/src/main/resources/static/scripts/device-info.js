function renderDeviceInfoFormData() {
    const deviceId = document.getElementById('id-input').textContent;
    getDevicePromise(deviceId).then(device => {
        document.getElementById('device-name').textContent = device.name;
        document.getElementById('device-model').textContent = device.deviceInfo.vendor + ' ' + device.deviceInfo.model;
        document.getElementById('device-description').textContent = device.description;
        renderAccessPointsTable(device);
    });
};

function renderAccessPointsTable(device) {
    const table = document.getElementById('access-points-table');
    const tbody = table.querySelector('tbody');
    device.accessPoints.forEach(accessPoint=> {
        const tableRow = document.createElement('tr');
        const propertiesCell = document.createElement('td');
        accessPoint.properties.forEach(property => {
            const propertyElem = document.createElement('p');
            propertyElem.innerHTML = `${property.type}`;
            propertiesCell.appendChild(propertyElem)});
        tableRow.innerHTML = `
            <td>${accessPoint.type}</td>
            <td>${accessPoint.band}</td>
            <td>${accessPoint.interfaceName}</td>
            ${propertiesCell.outerHTML}
            <td></td>
        `;
        tbody.appendChild(tableRow);
    });
};


