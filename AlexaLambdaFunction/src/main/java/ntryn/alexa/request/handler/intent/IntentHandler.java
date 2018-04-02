package ntryn.alexa.request.handler.intent;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.SpeechletResponseBuilder;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.request.handler.RequestHandler;

import static ntryn.alexa.common.Constants.QUIT_GAME;
import static ntryn.alexa.common.Constants.AVAILABILITY_REPROMPT;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
public abstract class IntentHandler implements RequestHandler<IntentRequest> {
    @Override
    public abstract SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope);

    protected Slot getTriggerSlot(Intent intent) {
        return intent.getSlot("trigger");
    }

    protected Slot getConfirmSlot(Intent intent) {
        return intent.getSlot("confirm");
    }

    protected Trigger getTriggerForMenuStage(Slot slot) {

        return null;
    }

    protected Trigger getTriggerForPlayStage(Slot slot, Game game) {

        return null;
    }

    protected SpeechletResponse skillNotLinkedSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse consoleNotLinkedSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse silentAvailRepromptSpeech(String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        // TODO
        return null;
    }

    protected SpeechletResponse onTriggerAvailRepromptSpeech(String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        // TODO
        return null;
    }

    protected SpeechletResponse onStopIntentSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse onStopIntentSpeech(Stage stage) {
        if(stage == null) {
            return onStopIntentSpeech();
        }

        // TODO
        return null;
    }

    protected SpeechletResponse quitGameAskConfirmationSpeech(Game game, String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, QUIT_GAME);
        // TODO
        return null;
    }

    protected SpeechletResponse silentEndSessionSpeech() {
        // TODO
        return null;
    }

    protected SpeechletResponse okAvailRepromtSpeech(String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        return SpeechletResponseBuilder.builder().speechText("Ok").repromptText("Hey, Are you there?").build();
    }

    protected SpeechletResponse availRepromptSpeech(boolean isUserAvailable, String requestId, SessionHelper sessionHelper) {
        if(isUserAvailable) {
            addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        }
        // TODO
        return null;
    }

    private void addConfirmation(String requestId, SessionHelper sessionHelper, String type) {
        Confirmation confirmation = new Confirmation();
        confirmation.setRequestId(requestId);
        confirmation.setType(type);
        sessionHelper.setConfirmation(confirmation);
    }
}
