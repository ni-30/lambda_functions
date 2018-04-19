package ntryn.alexa;

import com.amazonaws.regions.Regions;

public class AwsConfig {
    public static final String accountId = "127887160317";
    public static final Regions region = Regions.US_EAST_1;
    public static final String gcmPlatformArn = "arn:aws:sns:us-east-1:127887160317:app/GCM/AlexaFusionPlay";
    public static final String consoleWebUrl = "goo.gl/RVwSpK";
    public static final String GAME_CONTROL_TRIGGER_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/musical/amzn_sfx_bell_short_chime_01.mp3";
    public static final String GAME_CAROUSEL_TRIGGER_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/foley/amzn_sfx_glass_on_table_01.mp3";
    public static final String SELECT_GAME_TRIGGER_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/impacts/amzn_sfx_punch_01.mp3";
    public static final String GO_TO_MENU_TRIGGER_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/impacts/amzn_sfx_punch_01.mp3";
    public static final String RESET_TRIGGER_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/musical/amzn_sfx_bell_short_chime_01.mp3";
    public static final String SHUTDOWN_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/home/amzn_sfx_doorbell_01.mp3";
    public static final String SILENT_AUDIO = "https://s3.amazonaws.com/ask-soundlibrary/musical/amzn_sfx_bell_short_chime_01.mp3";
}
