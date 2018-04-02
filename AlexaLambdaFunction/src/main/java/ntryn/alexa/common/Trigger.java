package ntryn.alexa.common;

public enum Trigger {
    ALEXA_SESSION_STARTED, // when alexa new session is started
    RESET, // to reset the console and controller
    NEXT, PREV, SELECT, // menu stage
    UP, DOWN, LEFT, RIGHT, // action interface in play stage
    MENU, PLAY, PAUSE, RESUME, STOP // user interface in play stage
}
