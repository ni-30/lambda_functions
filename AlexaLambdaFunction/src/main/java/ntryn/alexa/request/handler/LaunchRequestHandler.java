package ntryn.alexa.request.handler;

import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Confirmation;
import ntryn.alexa.common.SessionHelper;
import ntryn.alexa.common.SpeechletResponseBuilder;

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
                return resumeGameAskConfirmationSpeech(requestEnvelope.getRequest(), sessionHelper);
            case MENU:
                return menuStageSpeech();
            default:
                break;
        }

        return noActiveConsoleFoundSpeech();
    }

    protected SpeechletResponse skillNotLinkedSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .cardTitle("Account Not Linked!")
                                       .speechText("Your account is not linked with the skill.  Try again after linking your account.")
                                       .cardContent("Link your account to continue.")
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse consoleNotLinkedSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("No linked console found.  Open your console in desktop browser, if already opened refresh the page and try again. The card with console url is sent in your app.")
                                       .cardTitle("Console Website URL")
                                       .cardContent(AwsConfig.consoleWebUrl)
                                       .shouldEndSession(true)
                                       .build();
    }

    protected SpeechletResponse menuStageSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("Select a game.")
                                       .repromptText("Hey! Are you there?")
                                       .build();
    }

    protected SpeechletResponse resumeGameAskConfirmationSpeech(LaunchRequest request, SessionHelper sessionHelper) {
        Confirmation confirmation = new Confirmation();
        confirmation.setRequestId(request.getRequestId());
        confirmation.setType(RESUME_GAME);
        sessionHelper.setConfirmation(confirmation);

        return SpeechletResponseBuilder.builder()
                                       .cardTitle("Game Is Paused!")
                                       .speechText("Game is currently paused. Do you want to resume the game? Say yes or no.")
                                       .repromptText("")
                                       .build();
    }

    protected SpeechletResponse noActiveConsoleFoundSpeech() {
        return SpeechletResponseBuilder.builder()
                                       .speechText("No active console found. Refresh your console page and try again.")
                                       .build();
    }

}
