package ntryn.alexa.request.handler.intent;

import lombok.extern.slf4j.Slf4j;
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
            return silentAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        Trigger trigger;
        switch (sessionHelper.getStage()) {
            case PLAY:
                trigger = getTriggerForMenuStage(slot);
                break;
            case MENU:
                trigger = getTriggerForPlayStage(slot, sessionHelper.getGame());
                break;
            default:
                trigger = null;
                break;
        }

        log.info("new trigger. stage={}, game={}, slotValue={}, trigger={}", sessionHelper.getStage(), sessionHelper.getGame(), slot.getValue(), trigger);

        if(trigger == null) {
            return silentAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        final ConsoleMessage msg = new ConsoleMessage();
        if(trigger == Trigger.MENU || trigger == Trigger.RESET) {
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

        return onTriggerAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
    }

}
