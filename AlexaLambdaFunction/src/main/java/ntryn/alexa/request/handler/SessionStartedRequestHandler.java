package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.dto.UserEntity;
import ntryn.alexa.lambda.skill.SkillContext;
import ntryn.alexa.service.DatabaseService;

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

        DatabaseService databaseService = SkillContext.context.getDatabaseService();

        final String uid = databaseService.convertAlexaTokenToUid(session.getUser().getAccessToken());

        SessionHelper sessionHelper = new SessionHelper(session);
        sessionHelper.setUid(uid);

        UserEntity userEntity = databaseService.get(uid);
        if(userEntity != null) {
            sessionHelper.setIsConsoleLinked(userEntity.getIsConsoleLinked() != null && userEntity.getIsConsoleLinked());
            sessionHelper.setStage(userEntity.getStage() == null ? Stage.MENU : userEntity.getStage());
            sessionHelper.setGame(userEntity.getGame() == null ? Game.values()[0] : userEntity.getGame());
            sessionHelper.setGcmEndpointArn(userEntity.getGcmEndpointArn());

            ConsoleMessage msg = new ConsoleMessage();
            msg.setGame(sessionHelper.getGame());
            msg.setStage(sessionHelper.getStage());

            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
        }

        return null;
    }

}
