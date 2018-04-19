package ntryn.alexa.lambda.skill;

import ntryn.alexa.annotation.Intent;
import ntryn.alexa.service.MessagingService;
import ntryn.alexa.request.handler.RequestHandler;
import ntryn.alexa.request.handler.IntentHandler;
import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.service.QueueService;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.services.DirectiveServiceClient;

@Slf4j
public class SkillContext {
    public static final String containerId = UUID.randomUUID().toString();
    public static final SkillContext context = new SkillContext();

    private final Map<String, IntentHandler> intentHandlerMap = new HashMap<>();
    private RequestHandler<SessionStartedRequest> sessionStartedRequestHandler;
    private RequestHandler<LaunchRequest> launchRequestRequestHandler;
    private RequestHandler<SessionEndedRequest> sessionEndedRequestHandler;
    private MessagingService messagingService;
    private DirectiveServiceClient directiveServiceClient;
    private QueueService queueService;

    void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }

    public QueueService getQueueService() {
        return queueService;
    }

    void setSessionStartedRequestHandler(RequestHandler<SessionStartedRequest> sessionStartedRequestHandler) {
        this.sessionStartedRequestHandler = sessionStartedRequestHandler;
    }

    void setLaunchRequestRequestHandler(RequestHandler<LaunchRequest> launchRequestRequestHandler) {
        this.launchRequestRequestHandler = launchRequestRequestHandler;
    }

    void setSessionEndedRequestHandler(RequestHandler<SessionEndedRequest> sessionEndedRequestHandler) {
        this.sessionEndedRequestHandler = sessionEndedRequestHandler;
    }

    void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    void setDirectiveServiceClient(DirectiveServiceClient directiveServiceClient) {
        this.directiveServiceClient = directiveServiceClient;
    }

    public RequestHandler<SessionStartedRequest> getSessionStartedRequestHandler() {
        return sessionStartedRequestHandler;
    }

    public RequestHandler<LaunchRequest> getLaunchRequestRequestHandler() {
        return launchRequestRequestHandler;
    }

    public RequestHandler<SessionEndedRequest> getSessionEndedRequestHandler() {
        return sessionEndedRequestHandler;
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

    public DirectiveServiceClient getDirectiveServiceClient() {
        return directiveServiceClient;
    }

    /**
     * add new Intent Handler
     * */
    void addIntentHandler(Class<? extends IntentHandler> cls) {
        Intent intentAnno = cls.getAnnotation(Intent.class);
        if(intentAnno == null) {
            throw new IllegalArgumentException("invalid intent handler class. Intent annotation not found. class - " + cls.getCanonicalName());
        }

        if(StringUtils.isBlank(intentAnno.value())) {
            throw new IllegalArgumentException("invalid intent handler class. Intent name is null/empty.");
        }

        if(intentHandlerMap.containsKey(intentAnno.value())) {
            throw new IllegalArgumentException("intent handler with duplicate name found. Intent - " + intentAnno.value());
        }

        IntentHandler intentHandler;
        try {
            intentHandler = cls.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            log.error("failed to construct context of handler. ", e);
            throw new IllegalArgumentException("invalid intent handler", e);
        }

        intentHandlerMap.put(intentAnno.value(), intentHandler);
    }

    public IntentHandler getIntentHandler(String intent) {
        return intentHandlerMap.get(intent);
    }
}
