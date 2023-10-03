package ru.oke.sinebot.keenetic.dto.rci.config.wifi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatusArray;

/**
 * Объект данного класса содержит результат изменения конфигурации точки доступа, получаемый от API Keenetic OS
 * Ответ API агрегирует статусы выполнения операций над каждой изменяемой сущностью конфигурации. В данном проекте
 * операции конфигурирования выполняются атомарно, поэтому этот класс описывает результат выполнения атомарной операции
 * изменения административного статуса точки доступа
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessPointStatusConfigResponse {
    /*
    результат выполнения операции изменения административного статуса точки доступа
     */
    private ActionEntityStatusArray up;
}
