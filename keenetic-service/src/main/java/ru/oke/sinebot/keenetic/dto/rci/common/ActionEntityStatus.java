package ru.oke.sinebot.keenetic.dto.rci.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Объект данного класса содержит статус завершения операции (оперативной или административной), выполненной над
 * каким-либо объектом через API Keenetic OS
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ActionEntityStatus {
    private ResultStatus status;

    private String code;

    private String ident;

    private String message;
}
