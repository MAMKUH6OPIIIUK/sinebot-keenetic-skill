package ru.oke.sinebot.keenetic.dto.rci.operational.wifi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;

import java.util.List;

/**
 * Класс описывает объект, получаемый в качестве ответа на запрос информации о состоянии точки доступа роутера Keenetic
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessPointStatusResponse {
    /*
    идентификатор интерфейса роутера
     */
    private String id;

    /*
    описание точки доступа
     */
    private String description;

    /*
    административный статус точки доступа
     */
    private AdminState state;

    /*
    имя сети Wi-Fi, запущенной на точке доступа. Представлено только если точка доступа включена
     */
    private String ssid;

    /*
    статус выполнения операции. Будет содержать ошибку выполнения, если переданы неверные параметры запроса (например,
    некорректное наименование точки доступа)
     */
    @JsonProperty("status")
    private List<ActionEntityStatus> actionStatus;
}
