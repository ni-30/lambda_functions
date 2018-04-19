package ntryn.alexa.request.handler;

import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Intent("HowToPlayIntent")
public class HowToPlayIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked()) {
            return skillNotLinkedSpeech();
        }

        if(!sessionHelper.isConsoleLinked()) {
            return consoleNotLinkedSpeech();
        }

        if(sessionHelper.getStage() == Stage.PLAY) {
            return sessionHelper.getGame().helpSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        return silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
    }
}
