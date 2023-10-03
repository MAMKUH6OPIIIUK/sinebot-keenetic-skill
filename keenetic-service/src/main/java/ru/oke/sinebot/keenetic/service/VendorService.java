package ru.oke.sinebot.keenetic.service;

import ru.oke.sinebot.keenetic.dto.api.info.VendorResponseDto;

import java.util.List;

public interface VendorService {
    public List<VendorResponseDto> findAll();
}
