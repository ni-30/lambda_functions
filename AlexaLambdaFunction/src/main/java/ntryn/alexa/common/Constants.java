package ntryn.alexa.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String QUIT_GAME = "quitGame";
    public static final String RESUME_GAME = "resumeGame";
    public static final String AVAILABILITY_REPROMPT = "availabilityReprompt";
    public static final String SKILL_DISPLAY_NAME = "Voicetick";
}
