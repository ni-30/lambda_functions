package ntryn.alexa.common;

import com.amazon.speech.speechlet.SpeechletResponse;

public enum Game {
    pingPong();

    Game() {
    }

    public SpeechletResponse helpSpeech() {
        // TODO
        return null;
    }

    public static Game getNextGame(Game currentGame) {
        Game[] games = Game.values();
        if(currentGame == null || (currentGame.ordinal() + 1) == games.length) {
            return games[0];
        }
        return games[currentGame.ordinal() + 1];
    }

    public static Game getPrevGame(Game currentGame) {
        Game[] games = Game.values();
        if(currentGame == null || (currentGame.ordinal() - 1) == -1) {
            return games[games.length - 1];
        }
        return games[currentGame.ordinal() - 1];
    }
}
