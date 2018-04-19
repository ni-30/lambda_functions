package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.Constants;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.SpeechletResponseBuilder;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;

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
        if(slot == null || slot.getValue() == null) return null;

        switch (slot.getValue()) {
            case "next":
                return Trigger.NEXT;
            case "previous":
            case "prev":
                return Trigger.PREV;
            case "select":
                return Trigger.SELECT;
            case "stop":
                return Trigger.STOP;
        }

        return null;
    }

    protected Trigger getTriggerForPlayStage(Slot slot, Game game) {
        if(slot == null || slot.getValue() == null || game == null) return null;

        switch (slot.getValue()) {
            case "play":
            case "replay":
                return Trigger.PLAY;
            case "pause":
                return Trigger.PAUSE;
            case "resume":
                return Trigger.RESUME;
            case "reset":
                return Trigger.RESET;
            case "stop":
                return Trigger.STOP;
            case "menu":
                return Trigger.MENU;
            default:
                break;
        }

        switch (game){
            case breakout:
                switch (slot.getValue()) {
                    case "left":
                        return Trigger.LEFT;
                    case "right":
                        return Trigger.RIGHT;
                    default:
                        break;
                }
                break;
            case puzzle_2048:
                switch (slot.getValue()) {
                    case "left":
                        return Trigger.LEFT;
                    case "right":
                        return Trigger.RIGHT;
                    case "up":
                        return Trigger.UP;
                    case "down":
                        return Trigger.DOWN;
                    default:
                        break;
                }
                break;

            case t_rex:
                switch (slot.getValue()) {
                    case "jump":
                    case "up":
                        return Trigger.UP;
                    case "down":
                        return Trigger.DOWN;
                    default:
                        break;
                }
                break;
        }

        return null;
    }

    protected SpeechletResponse skillNotLinkedSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("No account found for the request.  Try again after linking your account with the skill.")
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse consoleNotLinkedSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("No linked console found.  Open your console in desktop browser, if already opened refresh the page and try again. The card with console url is sent in your app.")
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse silentWithAvailRepromptSpeech(String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        // TODO : add silent audio
        return SpeechletResponseBuilder.builder()
                                       .audioUrl(AwsConfig.SILENT_AUDIO)
                                       .repromptText("Hey, Are you there?")
                                       .build();
    }

    protected SpeechletResponse onTriggerWithAvailRepromptSpeech(String requestId, SessionHelper sessionHelper, String audioUrl) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        // todo: add audio
        return SpeechletResponseBuilder.builder()
                                       .audioUrl(audioUrl)
                                       .repromptText("Hey, Are you there?")
                                       .build();
    }

    protected SpeechletResponse onShutdownIntentSpeech() {
        // Todo : add audio on shutdown
        return SpeechletResponseBuilder.builder()
                                       .speechText("Shutting down " + Constants.SKILL_DISPLAY_NAME)
                                       .audioUrl(AwsConfig.SHUTDOWN_AUDIO)
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse quitGameConfirmationSpeech(Game game, String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, QUIT_GAME);
        return SpeechletResponseBuilder.builder()
                                       .speechText("Do you want to quit the game? Say yes or no.")
                                       .repromptText("Do you want to quit the game?")
                                       .build();
    }

    protected SpeechletResponse silentEndSessionSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("")
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse ackConfirmationWithAvailRepromtSpeech(String requestId, SessionHelper sessionHelper) {
        addConfirmation(requestId, sessionHelper, AVAILABILITY_REPROMPT);
        return SpeechletResponseBuilder.builder().speechText("Ok").repromptText("Hey, Are you there?").build();
    }

    private void addConfirmation(String requestId, SessionHelper sessionHelper, String type) {
        Confirmation confirmation = new Confirmation();
        confirmation.setRequestId(requestId);
        confirmation.setType(type);
        confirmation.setTimestamp(System.currentTimeMillis());
        sessionHelper.setConfirmation(confirmation);
    }
}
