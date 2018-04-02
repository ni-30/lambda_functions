package ntryn.alexa.lambda.web;

import ntryn.alexa.service.DatabaseService;
import ntryn.alexa.service.MessagingService;

import java.util.UUID;

public class WebContext {
    public static final String containerId = UUID.randomUUID().toString();
    public static final WebContext context = new WebContext();

    private DatabaseService databaseService;
    private MessagingService messagingService;

    void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    void setMessagingService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public MessagingService getMessagingService() {
        return messagingService;
    }

}
