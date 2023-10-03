package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsStatusWrapper;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessPointWpsStatusResponse {
    private WpsStatusWrapper wps;
}
