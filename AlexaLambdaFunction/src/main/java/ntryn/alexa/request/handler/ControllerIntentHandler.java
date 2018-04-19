package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.lambda.skill.SkillContext;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
@Intent("ControllerIntent")
public class ControllerIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked()) {
            return skillNotLinkedSpeech();
        }

        if(!sessionHelper.isConsoleLinked()) {
            return consoleNotLinkedSpeech();
        }

        final Slot slot = getTriggerSlot(requestEnvelope.getRequest().getIntent());
        if(slot == null || slot.getValue() == null) {
            return silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        Trigger trigger;
        switch (sessionHelper.getStage()) {
            case PLAY:
                trigger = getTriggerForPlayStage(slot, sessionHelper.getGame());
                break;
            case MENU:
                trigger = getTriggerForMenuStage(slot);
                break;
            default:
                trigger = null;
                break;
        }

        log.info("new trigger. stage={}, game={}, slotValue={}, trigger={}", sessionHelper.getStage(), sessionHelper.getGame(), slot.getValue(), trigger);

        if(trigger == null) {
            return silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        final ConsoleMessage msg = new ConsoleMessage();
        if(trigger == Trigger.MENU || trigger == Trigger.RESET) {
            msg.setStage(sessionHelper.getStage());
            sessionHelper.setStage(Stage.MENU);
            if(trigger == Trigger.RESET) {
                sessionHelper.setGame(Game.values()[0]);
            }
        } else if (sessionHelper.getStage() == Stage.MENU) {
            if(trigger == Trigger.SELECT) {
                msg.setStage(Stage.MENU);
                sessionHelper.setStage(Stage.PLAY);
            } else if (trigger == Trigger.NEXT) {
                sessionHelper.setGame(Game.getNextGame(sessionHelper.getGame()));
            } else if (trigger == Trigger.PREV) {
                sessionHelper.setGame(Game.getPrevGame(sessionHelper.getGame()));
            }
        }

        if(msg.getGame() == null) msg.setGame(sessionHelper.getGame());
        if(msg.getStage() == null) msg.setStage(sessionHelper.getStage());
        msg.setTrigger(trigger);

        SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);

        SpeechletResponse response;
        if(trigger == Trigger.STOP) {
            response = new AmazonStopIntentHandler().handle(requestEnvelope);
        } else {
            response = onTriggerWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(),
                                                        sessionHelper,
                                                        getTriggerAudio(sessionHelper.getStage(), sessionHelper.getTrigger()));
        }
        sessionHelper.setTrigger(trigger, requestEnvelope.getRequest().getRequestId());
        return response;
    }

    private String getTriggerAudio(Stage stage, Trigger trigger) {
        if(stage == Stage.MENU) {
            if(trigger == Trigger.MENU) {
                return AwsConfig.GO_TO_MENU_TRIGGER_AUDIO;
            } else if(trigger == Trigger.RESET) {
                return AwsConfig.RESET_TRIGGER_AUDIO;
            } else {
                return AwsConfig.GAME_CAROUSEL_TRIGGER_AUDIO;
            }
        } else if (stage == Stage.PLAY) {
            if(trigger == Trigger.SELECT) {
                return AwsConfig.SELECT_GAME_TRIGGER_AUDIO;
            } else {
                return AwsConfig.GAME_CONTROL_TRIGGER_AUDIO;
            }
        } else {
            return null;
        }
    }
}
