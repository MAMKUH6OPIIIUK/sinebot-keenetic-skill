package ru.oke.sinebot.keenetic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.oke.sinebot.keenetic.dto.api.info.ModelResponseDto;
import ru.oke.sinebot.keenetic.service.ModelService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/model")
public class ModelController {
    private final ModelService modelService;

    @GetMapping
    public List<ModelResponseDto> findAll() {
        return this.modelService.findAll();
    }

    @GetMapping(params = "vendorId")
    public List<ModelResponseDto> findByVendorId(@RequestParam(name = "vendorId") Long vendorId) {
        return this.modelService.findByVendorId(vendorId);
    }
}
