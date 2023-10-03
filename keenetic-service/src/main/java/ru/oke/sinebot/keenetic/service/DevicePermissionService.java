package ru.oke.sinebot.keenetic.service;

public interface DevicePermissionService {
    void grantPermissions(Long id);

    void deletePermissions(Long id);
}
