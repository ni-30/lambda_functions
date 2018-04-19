package ntryn.alexa.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import ntryn.alexa.dto.LambdaHttpRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ntryn.alexa.common.Constants.*;

public class Utils {

    public static String serialize(Object object) {
        try {
            return object == null ? null : OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("invalid object. unable to serialize to string.");
        }
    }

    public static <T> T deserialize(Object object, Class<T> cls) {
        return OBJECT_MAPPER.convertValue(object, cls);
    }

    public static <T> T deserializeFromJsonString(String jsonStr, Class<T> cls) {
        try {
            return jsonStr == null ? null : OBJECT_MAPPER.readValue(jsonStr, cls);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid json string. unable to deserialize from string.", e);
        }
    }

    public static String convertAlexaTokenToUid(String token) {
        return token;
    }



}
