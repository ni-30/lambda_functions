package ntryn.alexa.lambda.skill;

import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.SpeechletResponseBuilder;
import ntryn.alexa.request.handler.intent.IntentHandler;
import lombok.extern.slf4j.Slf4j;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;

@Slf4j
public class SkillSpeechlet implements SpeechletV2 {

    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
        SkillContext.context.getSessionStartedRequestHandler().handle(requestEnvelope);
    }

    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());

        SpeechletResponse response = SkillContext.context.getLaunchRequestRequestHandler().handle(requestEnvelope);
        new SessionHelper(requestEnvelope.getSession()).setPreviousRequestId(requestEnvelope.getRequest().getRequestId());
        return response;
    }

    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        log.info("onIntent requestId={}, sessionId={}, value={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());

        Intent intent = requestEnvelope.getRequest().getIntent();
        if(intent == null || intent.getName() == null) {
            log.error("invalid intent. intent is null or name is null. requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
            new SessionHelper(requestEnvelope.getSession()).setPreviousRequestId(requestEnvelope.getRequest().getRequestId());
            return errorResponse("This is unsupported.  Please try something else.", null);
        }

        IntentHandler handler = SkillContext.context.getIntentHandler(intent.getName());
        if(handler == null) {
            log.error("invalid intent name. Intent handler not found for intent={}, requestId={}, sessionId={}", intent.getName(), requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
            new SessionHelper(requestEnvelope.getSession()).setPreviousRequestId(requestEnvelope.getRequest().getRequestId());
            return errorResponse("This is unsupported.  Please try something else.", null);
        }

        SpeechletResponse response = handler.handle(requestEnvelope);
        new SessionHelper(requestEnvelope.getSession()).setPreviousRequestId(requestEnvelope.getRequest().getRequestId());
        return response;
    }

    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());
        SkillContext.context.getSessionEndedRequestHandler().handle(requestEnvelope);
    }

    private SpeechletResponse errorResponse(String message, String cardTitle) {
        return SpeechletResponseBuilder.builder()
                                .cardTitle(cardTitle)
                                .speechText(message)
                                .build();
    }
}
