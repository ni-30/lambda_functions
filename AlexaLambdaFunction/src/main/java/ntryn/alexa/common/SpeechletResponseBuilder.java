package ntryn.alexa.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazon.speech.speechlet.Directive;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

public class SpeechletResponseBuilder {
    private String speechText;
    private String repromptText;
    private String cardTitle;
    private String cardContent;
    private String audioUrl;
    private boolean shouldEndSession = true;

    public static SpeechletResponseBuilder builder() {
        return new SpeechletResponseBuilder();
    }

    public SpeechletResponseBuilder speechText(String speechText) {
        this.speechText = speechText;
        return this;
    }

    public SpeechletResponseBuilder repromptText(String repromptText) {
        this.repromptText = repromptText;
        return this;
    }

    public SpeechletResponseBuilder cardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
        return this;
    }

    public SpeechletResponseBuilder cardContent(String cardContent) {
        this.cardContent = cardContent;
        return this;
    }

    public SpeechletResponseBuilder audioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
        return this;
    }

    public SpeechletResponseBuilder shouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
        return this;
    }

    public SpeechletResponse build() {
        OutputSpeech speech = audioUrl == null ? getPlainTextOutputSpeech(speechText) : getSsmlOutputSpeechWithAudio(speechText, audioUrl);

        if(repromptText == null) {
            SpeechletResponse response =  cardTitle == null
                   ? SpeechletResponse.newTellResponse(speech)
                   : SpeechletResponse.newTellResponse(speech, getSimpleCard(cardTitle, cardContent == null ? speechText : cardContent));
            response.setNullableShouldEndSession(shouldEndSession);
            return response;
        }

        return cardTitle == null
               ? SpeechletResponse.newAskResponse(speech, getReprompt(repromptText))
               : SpeechletResponse.newAskResponse(speech, getReprompt(repromptText), getSimpleCard(cardTitle, cardContent == null ? speechText : cardContent));
    }

    private static SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);
        return card;
    }

    private static PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        return speech;
    }

    private static SsmlOutputSpeech getSsmlOutputSpeechWithAudio(String speechText, String audioUrl) {
        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak><audio src='" + audioUrl + "'/>" +  (speechText == null ? "" : speechText) + "</speak>");
        speech.setId(UUID.randomUUID().toString());
        return speech;
    }

    private static Reprompt getReprompt(String repromptText) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(getPlainTextOutputSpeech(repromptText));
        return reprompt;
    }
}
