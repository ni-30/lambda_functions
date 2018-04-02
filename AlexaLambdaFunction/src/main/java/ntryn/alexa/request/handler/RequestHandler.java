package ntryn.alexa.request.handler;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.CoreSpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface RequestHandler<T extends CoreSpeechletRequest> {
    SpeechletResponse handle(SpeechletRequestEnvelope<T> requestEnvelope);
}
