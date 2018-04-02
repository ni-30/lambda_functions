package ntryn.alexa.request.handler;

import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import static ntryn.alexa.common.Constants.RESUME_GAME;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class LaunchRequestHandler implements RequestHandler<LaunchRequest> {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked()) {
            return skillNotLinkedSpeech();
        }

        if(!sessionHelper.isConsoleLinked()) {
            return consoleNotLinkedSpeech();
        }

        switch (sessionHelper.getStage()) {
            case PLAY:
                Confirmation confirmation = new Confirmation();
                confirmation.setRequestId(requestEnvelope.getRequest().getRequestId());
                confirmation.setType(RESUME_GAME);
                sessionHelper.setConfirmation(confirmation);
                return resumeGameAskConfirmationSpeech(sessionHelper.getGame());
            case MENU:
                return menuStageSpeech();
            default:
                break;
        }

        return endSession();
    }

    protected SpeechletResponse skillNotLinkedSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse consoleNotLinkedSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse menuStageSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse resumeGameAskConfirmationSpeech(Game game) {
        // TODO
        return null;
    }

    protected SpeechletResponse endSession() {
        // TOOD
        return null;
    }

}
