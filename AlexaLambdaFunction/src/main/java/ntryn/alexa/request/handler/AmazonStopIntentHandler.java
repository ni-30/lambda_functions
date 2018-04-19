package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.lambda.skill.SkillContext;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
@Intent("AMAZON.StopIntent")
public class AmazonStopIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked() || !sessionHelper.isConsoleLinked()) {
            return onShutdownIntentSpeech();
        }

        if(!(sessionHelper.getTrigger() == Trigger.STOP
                   && sessionHelper.getTriggerRequestId().equals(sessionHelper.getPreviousRequestId()))
                   && sessionHelper.getStage() == Stage.PLAY) {
            log.info("stopping game - {} | {} | {}", sessionHelper.getTrigger(), sessionHelper.getTriggerRequestId(), (sessionHelper.getPreviousRequestId()));

            ConsoleMessage msg = new ConsoleMessage();
            msg.setGame(sessionHelper.getGame());
            msg.setStage(Stage.PLAY);
            msg.setTrigger(Trigger.STOP);
            sessionHelper.setTrigger(Trigger.STOP, requestEnvelope.getRequest().getRequestId());

            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
            return onTriggerWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(),
                                                    sessionHelper,
                                                    AwsConfig.GAME_CONTROL_TRIGGER_AUDIO);
        }

        log.info("stopping skill");

        return onShutdownIntentSpeech();
    }

}
