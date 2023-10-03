function renderAllDevices() {
    getAllDevicesPromise()
        .then(devices => renderDevicesTableRows(devices))
};

function renderDevicesTableRows(devices) {
    const table = document.getElementById('smarthome-table');
    const tbody = table.querySelector('tbody');
    devices.forEach(device => renderDevicesTableRow(device, tbody))
};

function renderDevicesTableRow(device, tableRows) {
    const buttonsCell = generateNavigationButtonsCell(device);
    const tableRow = document.createElement('tr');
    tableRow.innerHTML = `
        <td hidden>${device.id}</td>
        <td>${device.deviceInfo.vendor} ${device.deviceInfo.model}</td>
        <td>${device.name}</td>
        <td>${device.description}</td>
        ${buttonsCell.outerHTML}
    `;
    tableRows.appendChild(tableRow);
};

function generateNavigationButtonsCell(device) {
    const buttonsCell = document.createElement('td');
    buttonsCell.innerHTML = `
        <button title="${viewButtonTitle}" class="device-table-button">
            <a href="/device/${device.id}">
                <img  src="/images/view-icon.png"/>
            </a>
        </button>
        <button title="${editButtonTitle}" class="device-table-button">
            <a href="/device/edit/${device.id}">
                <img  src="/images/edit-icon.png"/>
            </a>
        </button>
        <button title="${deleteButtonTitle}" class="device-table-button" onclick="deleteDevice(${device.id})">
            <img  src="/images/delete-icon.png"/>
        </button>
    `;
    return buttonsCell;
};