package ru.oke.sinebot.keenetic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oke.sinebot.keenetic.dto.api.info.VendorResponseDto;
import ru.oke.sinebot.keenetic.mapper.VendorMapper;
import ru.oke.sinebot.keenetic.repository.VendorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {
    private final VendorRepository vendorRepository;

    private final VendorMapper vendorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponseDto> findAll() {
        return this.vendorRepository.findAll().stream()
                .map(vendorMapper::mapToVendorResponseDto)
                .collect(Collectors.toList());
    }
}
