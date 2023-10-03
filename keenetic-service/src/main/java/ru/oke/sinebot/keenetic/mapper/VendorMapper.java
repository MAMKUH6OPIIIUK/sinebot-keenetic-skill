package ru.oke.sinebot.keenetic.mapper;

import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.info.VendorResponseDto;
import ru.oke.sinebot.keenetic.model.Vendor;

@Component
public class VendorMapper {
    public VendorResponseDto mapToVendorResponseDto(Vendor vendor) {
        VendorResponseDto dto = new VendorResponseDto();
        dto.setId(vendor.getId());
        dto.setName(vendor.getName());
        return dto;
    }
}
