package ntryn.alexa.request.handler;

import ntryn.alexa.AwsConfig;
import ntryn.alexa.annotation.Intent;
import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.Constants;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.SpeechletResponseBuilder;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

@Intent("AMAZON.HelpIntent")
public class AmazonHelpIntentHandler extends IntentHandler {

    @Override
    public SpeechletResponse handle(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        SessionHelper sessionHelper = new SessionHelper(requestEnvelope.getSession());
        if(!sessionHelper.isSkillLinked() || !sessionHelper.isConsoleLinked()) {
            return aboutSkillSpeech();
        }

        switch (sessionHelper.getStage()) {
            case MENU:
                return aboutMenuStageSpeech();
            case PLAY:
                return sessionHelper.getGame().helpSpeech(requestEnvelope.getRequest().getRequestId(), sessionHelper);
        }

        return aboutSkillSpeech();
    }

    private SpeechletResponse aboutSkillSpeech() {
        String speech = Constants.SKILL_DISPLAY_NAME + " is speech enabled game controller. You can play and control game on browser using voice command. The card with console url is sent to your app. ";
        return SpeechletResponseBuilder.builder()
                                       .speechText(speech)
                                       .cardTitle(Constants.SKILL_DISPLAY_NAME + " - Help")
                                       .cardContent(speech + "Console url - " +  AwsConfig.consoleWebUrl)
                                       .build();
    }

    private SpeechletResponse aboutMenuStageSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .cardTitle("Game carousel menu - Help")
                                       .speechText("Say NEXT or PREVIOUS to slide the game carousel.  And to select the game say SELECT.")
                                       .repromptText("Hey, are you there?")
                                       .build();
    }

    protected SpeechletResponse skillNotLinkedSpeech() {
        String speech = Constants.SKILL_DISPLAY_NAME + " is speech enabled game controller. You can play and control game on browser using voice command. The card with console url is sent to your app. Link you account to use the skill. ";
        return SpeechletResponseBuilder.builder()
                                       .speechText(speech)
                                       .shouldEndSession(true)
                                       .cardTitle(Constants.SKILL_DISPLAY_NAME + " - Help")
                                       .cardContent(speech + "Console url - " +  AwsConfig.consoleWebUrl)
                                       .build();
    }

    protected SpeechletResponse consoleNotLinkedSpeech() {
        String speech = Constants.SKILL_DISPLAY_NAME + " is speech enabled game controller. You can play and control game on browser using voice command. The card with console url is sent to your app.  Start by linking the console in desktop browser, if already opened refresh the page. ";
        return SpeechletResponseBuilder.builder()
                                       .speechText(speech)
                                       .speechText("")
                                       .shouldEndSession(true)
                                       .cardTitle(Constants.SKILL_DISPLAY_NAME + " - Help")
                                       .cardContent(speech + "Console url - " +  AwsConfig.consoleWebUrl)
                                       .build();
    }
}
