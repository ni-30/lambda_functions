package ntryn.alexa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ntryn.alexa.common.HttpMethod;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LambdaHttpRequest {
    private String body;
    private String resource;
    private String path;
    private HttpMethod httpMethod;
    private Map<String, String> queryStringParameters;
    private Map<String, String> headers;
    private Map<String, String> pathParameters;
    private Map<String, Object> stageVariables;
    private RequestContext requestContext;
    private Boolean isBase64Encoded;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestContext {
        private String resourceId;
        private String apiId;
        private String resourcePath;
        private HttpMethod httpMethod;
        private String requestId;
        private String accountId;
        private String stage;
        private Identity identity;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Identity {
        private String apiKey;
        private String userArn;
        private String cognitoAuthenticationType;
        private String caller;
        private String userAgent;
        private String user;
        private String cognitoIdentityPoolId;
        private String cognitoIdentityId;
        private String cognitoAuthenticationProvider;
        private String sourceIp;
        private String accountId;
    }
}
