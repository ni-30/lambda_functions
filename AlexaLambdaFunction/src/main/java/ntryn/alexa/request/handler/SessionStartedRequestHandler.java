package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Utils;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.dto.UserEntity;
import ntryn.alexa.lambda.skill.SkillContext;
import ntryn.alexa.service.QueueService;

import static ntryn.alexa.common.Trigger.ALEXA_SESSION_STARTED;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
public class SessionStartedRequestHandler implements RequestHandler<SessionStartedRequest> {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        Session session = requestEnvelope.getSession();
        log.info("new session started. appId={}, sessionId={}", session.getApplication().getApplicationId(), session.getSessionId());

        if(session.getUser().getAccessToken() == null) {
            log.info("user account is not linked to skill. alexaUserId={}", session.getUser().getUserId());
            return null;
        }

        final String uid = Utils.convertAlexaTokenToUid(session.getUser().getAccessToken());

        SessionHelper sessionHelper = new SessionHelper(session);
        sessionHelper.setUid(uid);

        UserEntity userEntity = SkillContext.context.getQueueService().pull(uid, 3);
        if(userEntity != null && userEntity.getGcmEndpointArn() != null) {
            sessionHelper.setIsConsoleLinked(userEntity.getIsConsoleLinked() != null && userEntity.getIsConsoleLinked());
            sessionHelper.setStage(userEntity.getStage() == null ? Stage.MENU : userEntity.getStage());
            sessionHelper.setGame(userEntity.getGame() == null ? Game.values()[0] : userEntity.getGame());
            sessionHelper.setGcmEndpointArn(userEntity.getGcmEndpointArn());

            ConsoleMessage msg = new ConsoleMessage();
            msg.setGame(sessionHelper.getGame());
            msg.setStage(sessionHelper.getStage());
            msg.setTrigger(ALEXA_SESSION_STARTED);

            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
        }

        return null;
    }

}
