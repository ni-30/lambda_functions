package ntryn.alexa.common;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

public class SpeechletResponseBuilder {
    private String speechText;
    private String repromptText;
    private String cardTitle;
    private String cardContent;
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

    public SpeechletResponseBuilder shouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
        return this;
    }

    public SpeechletResponse build() {
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

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

    private static Reprompt getReprompt(String repromptText) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(getPlainTextOutputSpeech(repromptText));
        return reprompt;
    }
}
