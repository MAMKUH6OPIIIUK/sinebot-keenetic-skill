package ru.oke.sinebot.keenetic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oke.sinebot.keenetic.dto.api.info.ModelResponseDto;
import ru.oke.sinebot.keenetic.mapper.ModelMapper;
import ru.oke.sinebot.keenetic.repository.ModelRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ModelResponseDto> findAll() {
        return this.modelRepository.findAll().stream()
                .map(modelMapper::mapToModelResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModelResponseDto> findByVendorId(Long vendorId) {
        return this.modelRepository.findByVendorId(vendorId).stream()
                .map(modelMapper::mapToModelResponseDto)
                .collect(Collectors.toList());
    }
}
