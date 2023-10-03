package ru.oke.sinebot.keenetic.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.boot.jackson.JsonComponent;
import ru.oke.sinebot.keenetic.dto.rci.common.ActionEntityStatus;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsStatusWrapper;
import ru.oke.sinebot.keenetic.dto.rci.operational.wifi.wps.WpsStatus;

import java.io.IOException;
import java.util.List;

/**
 * Кастомный десериализатор для ответов, получаемых от API Keenetic OS на запросы статуса WPS
 * Ответ может содержать поле "status", которое может являться объектом, содержащим информацию о статусе WPS на
 * точке доступа, либо являться списком статусов выполнения операции, содержащим ошибки выполнения. Второй вариант
 * возможен, если, например, передать в запросе наименование не существующей точки доступа
 *
 * @author k.oshoev
 */
@JsonComponent
public class WpsStatusDeserializer extends JsonDeserializer<WpsStatusWrapper> {

    @Override
    public WpsStatusWrapper deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        TreeNode treeNode = parser.getCodec().readTree(parser);
        TreeNode status = treeNode.get("status");
        if (status != null) {
            ObjectMapper mapper = new ObjectMapper();
            WpsStatusWrapper resultObject = new WpsStatusWrapper();
            if (status.isObject()) {
                WpsStatus wpsStatus = mapper.treeToValue(status, WpsStatus.class);
                resultObject.setWpsStatus(wpsStatus);
            } else if (status.isArray()) {
                CollectionType typeReference = TypeFactory.defaultInstance()
                        .constructCollectionType(List.class, ActionEntityStatus.class);
                List<ActionEntityStatus> actionStatus = mapper.treeToValue(status, typeReference);
                resultObject.setActionStatus(actionStatus);
            }
            return resultObject;
        }
        return null;
    }
}
