package ntryn.alexa.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LambdaHttpResponse {
    private Boolean isBase64Encoded;
    private Integer statusCode;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}
