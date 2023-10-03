package ru.oke.sinebot.keenetic.dto.rci.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Объект класса может быть сериализован в пустой объект JSON. Подобные объекты довольно широко используются в
 * API роутеров Keenetic
 *
 * @author k.oshoev
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmptyObject {
}
