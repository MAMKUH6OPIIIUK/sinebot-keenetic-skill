package ru.oke.sinebot.yandex.mapper;

import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.DeviceInfoDto;
import ru.oke.sinebot.yandex.dto.DeviceInfo;

@Component
public class DeviceInfoMapper {
    public DeviceInfo mapToDeviceInfo(DeviceInfoDto deviceInfoDto) {
        return new DeviceInfo(deviceInfoDto.getVendor(), deviceInfoDto.getModel());
    }
}
