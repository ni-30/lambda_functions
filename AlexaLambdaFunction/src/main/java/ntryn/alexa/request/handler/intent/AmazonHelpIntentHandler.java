package ntryn.alexa.request.handler.intent;

import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.SessionHelper;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Intent("AMAZON.HelpIntent")
public class AmazonHelpIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());

        if(sessionHelper.isSkillLinked() && sessionHelper.isConsoleLinked()) {
            switch (sessionHelper.getStage()) {
                case MENU:
                    return aboutMenuStageSpeech();
                case PLAY:
                    return sessionHelper.getGame().helpSpeech();
            }
        }

        return aboutSkillSpeech();
    }

    private SpeechletResponse aboutSkillSpeech() {
        // TODO
        return null;
    }

    private SpeechletResponse aboutMenuStageSpeech() {
        // TODO
        return null;
    }

}
