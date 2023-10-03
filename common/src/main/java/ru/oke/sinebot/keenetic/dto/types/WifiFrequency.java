package ru.oke.sinebot.keenetic.dto.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Перечисление известных частотных диапазонов Wi-Fi
 *
 * @author k.oshoev
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum WifiFrequency {
    BAND_2_4("2.4"),
    BAND_5("5"),
    BAND_6("6");

    @JsonValue
    private final String band;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WifiFrequency from(String band) {
        for (WifiFrequency knownFreq : WifiFrequency.values()) {
            if (knownFreq.band.equalsIgnoreCase(band)) {
                return knownFreq;
            }
        }
        return null;
    }
}
