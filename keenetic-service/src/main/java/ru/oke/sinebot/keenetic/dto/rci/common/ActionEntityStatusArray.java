package ru.oke.sinebot.keenetic.dto.rci.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Объект данного класса содержит набор статусов управляющей операции, выполненной над каким-либо объектом через
 * API Keenetic OS. Как правило, содержит всего один статус
 *
 * @author k.oshoev
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ActionEntityStatusArray {
    private List<ActionEntityStatus> status;
}
