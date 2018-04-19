package ntryn.alexa.request.handler;

import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Trigger;
import ntryn.alexa.dto.ConsoleMessage;
import ntryn.alexa.lambda.skill.SkillContext;

import static ntryn.alexa.common.Constants.QUIT_GAME;
import static ntryn.alexa.common.Constants.AVAILABILITY_REPROMPT;
import static ntryn.alexa.common.Constants.RESUME_GAME;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Slf4j
@Intent("ConfirmationIntent")
public class ConfirmationIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isConsoleLinked() || !sessionHelper.isSkillLinked()) {
            return silentEndSessionSpeech();
        }

        Slot slot = getConfirmSlot(requestEnvelope.getRequest().getIntent());
        if(slot == null || !("no".equalsIgnoreCase(slot.getValue()) || "yes".equalsIgnoreCase(slot.getValue()))) {
            return sessionHelper.isNewSession() ? silentEndSessionSpeech() : silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        Confirmation confirmation = sessionHelper.getConfirmation();
        if(confirmation == null) {
            return sessionHelper.isNewSession() ? silentEndSessionSpeech() : silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        try {
            if(confirmation.getRequestId().equals(sessionHelper.getPreviousRequestId())) {
                switch (confirmation.getType()) {
                    case QUIT_GAME:
                        if(sessionHelper.getStage() != Stage.PLAY) {
                            break;
                        }

                        if ("yes".equalsIgnoreCase(slot.getValue())) {
                            sessionHelper.setStage(Stage.MENU);

                            ConsoleMessage msg = new ConsoleMessage();
                            msg.setGame(sessionHelper.getGame());
                            msg.setStage(Stage.MENU);
                            msg.setTrigger(Trigger.MENU);
                            sessionHelper.setTrigger(Trigger.MENU, requestEnvelope.getRequest().getRequestId());

                            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
                            return ackConfirmationWithAvailRepromtSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
                        } else {
                            ConsoleMessage msg = new ConsoleMessage();
                            msg.setGame(sessionHelper.getGame());
                            msg.setStage(Stage.PLAY);
                            msg.setTrigger(Trigger.RESUME);
                            sessionHelper.setTrigger(Trigger.RESUME, requestEnvelope.getRequest().getRequestId());

                            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
                            return silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
                        }

                    case RESUME_GAME:
                        if(sessionHelper.getStage() != Stage.PLAY) {
                            break;
                        }

                        if ("yes".equalsIgnoreCase(slot.getValue())) {
                            ConsoleMessage msg = new ConsoleMessage();
                            msg.setGame(sessionHelper.getGame());
                            msg.setStage(Stage.PLAY);
                            msg.setTrigger(Trigger.RESUME);
                            sessionHelper.setTrigger(Trigger.RESUME, requestEnvelope.getRequest().getRequestId());

                            SkillContext.context.getMessagingService().publishWithTargetArn(sessionHelper.getGcmEndpointArn(), msg);
                            return ackConfirmationWithAvailRepromtSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
                        } else {
                            return silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
                        }

                    case AVAILABILITY_REPROMPT:
                        if ("yes".equalsIgnoreCase(slot.getValue())) {
                            return ackConfirmationWithAvailRepromtSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
                        } else {
                            return silentEndSessionSpeech();
                        }
                }
            }
        } finally {
            sessionHelper.removeConfirmation();
        }

        return sessionHelper.isNewSession() ? silentEndSessionSpeech() : silentWithAvailRepromptSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
    }

}
