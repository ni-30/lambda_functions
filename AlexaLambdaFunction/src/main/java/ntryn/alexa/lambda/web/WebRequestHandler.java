package ntryn.alexa.lambda.web;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Utils;
import ntryn.alexa.dto.LambdaHttpRequest;
import ntryn.alexa.dto.LambdaHttpResponse;
import ntryn.alexa.dto.UserEntity;
import ntryn.alexa.lambda.AbstractRequestStreamHandler;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for Lambda function - AlexaFusionPlayConsoleService
 * */
@Slf4j
public class WebRequestHandler extends AbstractRequestStreamHandler<LambdaHttpRequest, LambdaHttpResponse> {
    static {
        WebContextLoader.load();
    }

    @Override
    protected LambdaHttpResponse handle(LambdaHttpRequest request, Context context) {
        log.info("new request. requestId={}, httpMethod={}, path={}, LambdaHttpRequest={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath(), Utils.serialize(request));
        if(request.getPath() == null || request.getHttpMethod() == null) {
            return failureResponse(500, "unable to parse request");
        }

        switch (request.getHttpMethod()) {
            case GET:
                return getMethod(request);
            case POST:
                return postMethod(request);
            case PUT:
                return putMethod(request);
            default:
                return failureResponse(400, "invalid http method");
        }
    }

    private LambdaHttpResponse getMethod(LambdaHttpRequest request) {
        switch (request.getPath()) {
            case "/console":
                final String uid = getUid(request.getQueryStringParameters());
                if(uid == null || uid.isEmpty()) {
                    log.info("uid is null/empty. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "query param - uid is null/empty");
                }

                UserEntity entity = WebContext.context.getQueueService().pull(uid, 5);
                if(entity == null) {
                    log.info("user not found. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(404, "user not found for uid - " + uid);
                }
                if(!WebContext.context.getQueueService().push("final", uid, Utils.deserialize(entity, Map.class))) {
                    return failureResponse(500, "failed to create final data");
                }
                if(entity.getStage() == null) entity.setStage(Stage.MENU);
                if(entity.getGame() == null) entity.setGame(Game.values()[0]);
                entity.setGcmEndpointArn(null);
                entity.setEmailId(null);
                entity.setUid(null);
                return successResponse(Utils.serialize(entity));
            default:
                return failureResponse(400, "invalid path for GET http method");
        }
    }

    private LambdaHttpResponse putMethod(LambdaHttpRequest request) {
        switch (request.getPath()) {
            case "/console":
                final String uid = getUid(request.getQueryStringParameters());
                if(uid == null || uid.isEmpty()) {
                    log.info("uid is null/empty. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "query param - uid is null/empty");
                }

                Map<String, Object> updateEntity = Utils.deserializeFromJsonString(request.getBody(), Map.class);

                final String gcmToken = getAndRemoveGcmToken(updateEntity);
                if(!StringUtils.isBlank(gcmToken)) {
                    String endpointArn = getGcmEndpointArn(gcmToken);
                    if(endpointArn == null) {
                        log.info("invalid gcm token={} for requestId={}, httpMethod={}, path={}", gcmToken, request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                        return failureResponse(400, "invalid gcm token");
                    }
                    putGcmEndpointArn(updateEntity, endpointArn);
                } else {
                    removeGcmEndpointArn(updateEntity);
                }


                if(updateEntity.isEmpty()) {
                    log.info("request body empty. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "empty request body");
                }

                if(!WebContext.context.getQueueService().push("consoleLinked", uid, updateEntity)) {
                    return failureResponse(404, "user not found");
                }

                return successResponse(null);
            default:
                return failureResponse(400, "invalid path for PUT http method");
        }
    }

    private LambdaHttpResponse postMethod(LambdaHttpRequest request) {
        switch (request.getPath()) {
            case "/console":
                final String uid = getUid(request.getQueryStringParameters());
                if(uid == null || uid.isEmpty()) {
                    log.info("uid is null/empty. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "query param - uid is null/empty");
                }

                final Map<String, Object> entity = Utils.deserializeFromJsonString(request.getBody(), Map.class);
                if(StringUtils.isBlank(getEmailId(entity))) {
                    log.info("emailId is null/empty. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "emailId is null/empty in request body");
                }

                final String gcmToken = getAndRemoveGcmToken(entity);
                if(StringUtils.isBlank(gcmToken)) {
                    log.info("gcm token not found. requestId={}, httpMethod={}, path={}", request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "gcm token not found");
                }
                String endpointArn = getGcmEndpointArn(gcmToken);
                if(endpointArn == null) {
                    log.info("invalid gcm token={} for requestId={}, httpMethod={}, path={}", gcmToken, request.getRequestContext().getRequestId(), request.getHttpMethod(), request.getPath());
                    return failureResponse(400, "invalid gcm token");
                }
                putGcmEndpointArn(entity, endpointArn);
                entity.put("isConsoleLinked", true);

                if(WebContext.context.getQueueService().createQueue(uid) == null) {
                    return failureResponse(500, "failed to create sqs queue");
                }
                if(!WebContext.context.getQueueService().push("consoleLinked", uid, entity)) {
                    return failureResponse(500, "failed to push to sqs queue");
                }
                return successResponse(null);

            case "/authorization":
                if(request.getBody() == null || !request.getBody().contains("code=")) {
                    return failureResponse(400, "invalid request");
                }

                Map<String, Object> response = new HashMap<>();
                String[] queryParams = request.getBody().split("&");
                for(String kv : queryParams) {
                    String[] arr = kv.split("=");
                    if("code".equals(arr[0])) {
                        if(arr.length != 2) {
                            return failureResponse(400, "invalid request");
                        }
                        response.put("access_token", arr[1]);
                        response.put("refresh_token", arr[1]);
                        break;
                    }
                }
                response.put("token_type", "Bearer");
                response.put("expires_in", 10368000);
                return successResponse(Utils.serialize(response));
            default:
                return failureResponse(400, "invalid path for POST http method");
        }
    }

    private LambdaHttpResponse failureResponse(int statusCode, String message) {
        LambdaHttpResponse response = new LambdaHttpResponse();
        response.setStatusCode(statusCode);
        response.setIsBase64Encoded(true);
        response.getHeaders().put("Access-Control-Allow-Origin", "*");
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        response.setBody(Utils.serialize(body));

        return response;
    }

    private LambdaHttpResponse successResponse(String body) {
        LambdaHttpResponse response = new LambdaHttpResponse();
        response.setStatusCode(200);
        response.setIsBase64Encoded(true);
        response.getHeaders().put("Access-Control-Allow-Origin", "*");
        if(body == null) {
            Map map = new HashMap();
            map.put("status", "success");
            body = Utils.serialize(map);
        }
        response.setBody(body);
        return response;
    }

    private String getAndRemoveGcmToken(Map<String, Object> requestBody) {
        if(requestBody == null || requestBody.isEmpty()) return null;
        return (String) requestBody.remove("gcmToken");
    }

    private String getUid(Map<String, String> queryStringParams) {
        if(queryStringParams == null) return null;
        return queryStringParams.get("uid");
    }

    private String getEmailId(Map<String, Object> requestBody) {
        if(requestBody == null) return null;
        return (String) requestBody.get("emailId");
    }

    private void putGcmEndpointArn(Map<String, Object> requestBody, String gcmEndpointArn) {
        requestBody.put("gcmEndpointArn", gcmEndpointArn);
    }

    private void removeGcmEndpointArn(Map<String, Object> requestBody) {
        if(requestBody == null) return;
        requestBody.remove("gcmEndpointArn");
    }

    private String getGcmEndpointArn(String gcmToken) {
        return WebContext.context.getMessagingService().createPlatformEndpoint(AwsConfig.gcmPlatformArn, gcmToken, null);
    }

    @Override
    protected Class<LambdaHttpRequest> getInputClass() {
        return LambdaHttpRequest.class;
    }
}
