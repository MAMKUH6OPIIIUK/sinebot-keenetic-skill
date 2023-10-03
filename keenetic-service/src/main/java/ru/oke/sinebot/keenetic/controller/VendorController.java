package ru.oke.sinebot.keenetic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.oke.sinebot.keenetic.dto.api.info.VendorResponseDto;
import ru.oke.sinebot.keenetic.service.VendorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vendor")
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    public List<VendorResponseDto> findAll() {
        return this.vendorService.findAll();
    }
}
