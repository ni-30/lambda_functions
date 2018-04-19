package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
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
@Intent("ShutdownIntent")
public class ShutdownIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked() || !sessionHelper.isConsoleLinked()) {
            return silentEndSessionSpeech();
        }

        if(sessionHelper.getStage() == Stage.PLAY) {
            ConsoleMessage msg = new ConsoleMessage();
            msg.setGame(sessionHelper.getGame());
            msg.setStage(Stage.PLAY);
            msg.setTrigger(Trigger.STOP);
            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
            sessionHelper.setTrigger(Trigger.STOP, requestEnvelope.getRequest().getRequestId());
        }

        return onShutdownIntentSpeech();
    }

}
