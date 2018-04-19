package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.lambda.skill.SkillContext;

import java.util.HashMap;
import java.util.Map;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
public class SessionEndedRequestHandler implements RequestHandler<SessionEndedRequest> {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("ending session. appId={}, sessionId={}", requestEnvelope.getSession().getApplication().getApplicationId(), requestEnvelope.getSession().getSessionId());

        processEndingSession(requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession());

        return null;
    }

    public void processEndingSession(String requestId, Session session) {
        SessionHelper sessionHelper = new SessionHelper(session);
        if(sessionHelper.getUid() == null || !sessionHelper.isConsoleLinked()) {
            log.info("ending session. empty session.");
            return;
        }

        if(sessionHelper.getStage() == Stage.PLAY && sessionHelper.getTrigger() != Trigger.STOP) {
            ConsoleMessage msg = new ConsoleMessage();
            msg.setStage(sessionHelper.getStage());
            msg.setGame(sessionHelper.getGame());
            msg.setTrigger(Trigger.PAUSE);
            sessionHelper.setTrigger(Trigger.PAUSE, requestId);

            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
            log.info("triggered to pause game on session ending");
        }

        if(sessionHelper.getUid() != null && sessionHelper.getGcmEndpointArn() != null) {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("uid", sessionHelper.getUid());
            updateData.put("gcmEndpointArn", sessionHelper.getGcmEndpointArn());
            updateData.put("isConsoleLinked", sessionHelper.isConsoleLinked());
            updateData.put("stage", sessionHelper.getStage().name());
            updateData.put("game", sessionHelper.getGame().name());

            SkillContext.context.getQueueService().push("final", sessionHelper.getUid(), updateData);
            log.info("pushed on ending session");
        }
    }

}
