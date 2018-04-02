package ntryn.alexa.lambda.web;

import ntryn.alexa.service.DatabaseService;
import ntryn.alexa.service.MessagingService;

/**
 * App initializer
 * */
public class WebContextLoader {

    /**
     * load WebContext
     * */
    public static void load() {
        WebContext.context.setDatabaseService(new DatabaseService());
        WebContext.context.setMessagingService(new MessagingService());
    }

}
