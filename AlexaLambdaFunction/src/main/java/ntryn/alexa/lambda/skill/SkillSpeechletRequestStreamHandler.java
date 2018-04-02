package ntryn.alexa.lambda.skill;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class SkillSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds;
    static {
        supportedApplicationIds = new HashSet<>();
        SkillContextLoader.load();
    }

    public SkillSpeechletRequestStreamHandler() {
        super(new SkillSpeechlet(), supportedApplicationIds);
    }

}
