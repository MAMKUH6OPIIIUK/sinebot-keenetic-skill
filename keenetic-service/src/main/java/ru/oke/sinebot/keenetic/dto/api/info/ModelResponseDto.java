package ru.oke.sinebot.keenetic.dto.api.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseDto {
    private Long id;

    private VendorResponseDto vendor;

    private String name;

    private List<AccessPointResponseDto> accessPoints;

    private List<ActionDto> supportedActions;
}
