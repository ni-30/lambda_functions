package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.lambda.skill.SkillContext;
import ntryn.alexa.service.DatabaseService;
import java.util.HashMap;
import java.util.Map;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
public class SessionEndedRequestHandler implements RequestHandler<SessionEndedRequest> {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("ending session. appId={}, sessionId={}", requestEnvelope.getSession().getApplication().getApplicationId(), requestEnvelope.getSession().getSessionId());

        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(sessionHelper.getUid() == null) {
            return null;
        }

        if(sessionHelper.getStage() == Stage.PLAY) {
            ConsoleMessage msg = new ConsoleMessage();
            msg.setStage(sessionHelper.getStage());
            msg.setGame(sessionHelper.getGame());
            msg.setTrigger(Trigger.PAUSE);

            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
            log.info("triggered to pause game on session ending");
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("game", sessionHelper.getGame().name());
        updateData.put("stage", sessionHelper.getStage().name());

        DatabaseService databaseService = SkillContext.context.getDatabaseService();
        try {
            databaseService.update(sessionHelper.getUid(), updateData);
        } catch (Exception e) {
            log.error("failed to persist ending session data", e);
        }

        return null;
    }

}
