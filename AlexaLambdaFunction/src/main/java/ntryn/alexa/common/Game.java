package ntryn.alexa.common;

import static ntryn.alexa.common.Constants.AVAILABILITY_REPROMPT;
import com.amazon.speech.speechlet.SpeechletResponse;

public enum Game {
    breakout(true, "2D Breakout", "Say left or right to move paddle."),
    puzzle_2048(true, "2048", "Say left, right, up or down to move tiles."),
    t_rex(false, "T-Rex", "Say jump or down to control the game.");

    private final boolean isEnabled;
    private final String name;
    private final String help;
    Game(boolean isEnabled, String name, String help) {
        this.isEnabled = isEnabled;
        this.name = name;
        this.help = help;
    }

    public SpeechletResponse helpSpeech(String requestId, SessionHelper sessionHelper) {
        Confirmation confirmation = new Confirmation();
        confirmation.setRequestId(requestId);
        confirmation.setType(AVAILABILITY_REPROMPT);
        confirmation.setTimestamp(System.currentTimeMillis());
        sessionHelper.setConfirmation(confirmation);

        return SpeechletResponseBuilder.builder()
                                       .cardTitle(this.name + " - Help")
                                       .speechText(this.help)
                                       .repromptText("Hey, Are you still there?")
                                       .build();
    }

    public static Game getNextGame(Game currentGame) {
        Game[] games = Game.values();
        if(currentGame == null || (currentGame.ordinal() + 1) == games.length) {
            return games[0];
        }
        Game game = games[currentGame.ordinal() + 1];
        if(game.isEnabled) {
            return game;
        } else {
            return getNextGame(game);
        }
    }

    public static Game getPrevGame(Game currentGame) {
        Game[] games = Game.values();
        if(currentGame == null || (currentGame.ordinal() - 1) == -1) {
            return games[games.length - 1];
        }
        Game game = games[currentGame.ordinal() - 1];
        if(game.isEnabled) {
            return game;
        } else {
            return getPrevGame(game);
        }
    }
}
