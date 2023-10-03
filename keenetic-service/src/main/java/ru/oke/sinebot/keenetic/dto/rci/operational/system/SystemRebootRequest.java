package ru.oke.sinebot.keenetic.dto.rci.operational.system;

import lombok.Getter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.EmptyObject;

/**
 * Класс описывает объект, который может использоваться в качестве тела запроса на перезагрузку роутера Keenetic
 * Объект данного класса можно в чистом виде отправлять на следующий endpoint роутера:
 * rci/system
 *
 * @author k.oshoev
 */
@Getter
@ToString
public class SystemRebootRequest {

    private final EmptyObject reboot;

    /**
     * Конструктор по умолчанию. Никаких аргументов для создания корректного объекта не требуется
     */
    public SystemRebootRequest() {
        this.reboot = new EmptyObject();
    }
}
