package ntryn.alexa.lambda.web;

import ntryn.alexa.service.MessagingService;
import ntryn.alexa.service.QueueService;

import java.util.UUID;

public class WebContext {
    public static final String containerId = UUID.randomUUID().toString();
    public static final WebContext context = new WebContext();

    private MessagingService messagingService;
    private QueueService queueService;

    void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }


    void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }

    public QueueService getQueueService() {
        return queueService;
    }
}
