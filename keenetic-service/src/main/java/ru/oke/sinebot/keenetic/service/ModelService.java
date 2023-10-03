package ru.oke.sinebot.keenetic.service;

import ru.oke.sinebot.keenetic.dto.api.info.ModelResponseDto;

import java.util.List;

public interface ModelService {
    List<ModelResponseDto> findAll();

    List<ModelResponseDto> findByVendorId(Long vendorId);
}
