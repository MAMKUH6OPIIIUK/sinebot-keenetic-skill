package ru.oke.sinebot.keenetic.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oke.sinebot.keenetic.dto.api.ErrorCode;

/**
 * DTO с информацией об ошибке. Может применяться для запросов статусов устройств или изменения статусов. Может
 * применяться как ко всей операции над устройством в целом, так и отдельным операциям над свойствами устройства
 *
 * @author k.oshoev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private ErrorCode errorCode;

    private String errorMessage;
}
