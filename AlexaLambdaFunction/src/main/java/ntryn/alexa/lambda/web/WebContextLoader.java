package ntryn.alexa.lambda.web;

import ntryn.alexa.service.MessagingService;
import ntryn.alexa.service.QueueService;

/**
 * App initializer
 * */
public class WebContextLoader {

    /**
     * load WebContext
     * */
    public static void load() {
        WebContext.context.setQueueService(new QueueService());
        WebContext.context.setMessagingService(new MessagingService());
    }

}
