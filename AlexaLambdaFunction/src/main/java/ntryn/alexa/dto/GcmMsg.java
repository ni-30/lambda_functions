package ntryn.alexa.dto;

import ntryn.alexa.common.Utils;

import java.util.HashMap;
import java.util.Map;

public interface GcmMsg {
    default String toGcmMsg() {
        Map<String, String> data = new HashMap<>();
        data.put("default", Utils.serialize(this));
        return Utils.serialize(data);
    }
}
