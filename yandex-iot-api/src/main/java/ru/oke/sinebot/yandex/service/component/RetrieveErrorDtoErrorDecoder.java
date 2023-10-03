package ru.oke.sinebot.yandex.service.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.keenetic.dto.api.ErrorDto;
import ru.oke.sinebot.yandex.exception.KeeneticServiceException;

import java.io.InputStream;

@Component
public class RetrieveErrorDtoErrorDecoder implements ErrorDecoder {
    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorDto errorDto = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            errorDto = mapper.readValue(bodyIs, ErrorDto.class);
        } catch (Exception e) {
            return errorDecoder.decode(methodKey, response);
        }
        if (errorDto != null) {
            return new KeeneticServiceException(errorDto);
        } else {
            return errorDecoder.decode(methodKey, response);
        }
    }
}
