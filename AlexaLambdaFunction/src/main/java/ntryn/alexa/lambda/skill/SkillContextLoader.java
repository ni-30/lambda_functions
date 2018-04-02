package ntryn.alexa.lambda.skill;

import ntryn.alexa.request.handler.intent.ConfirmationIntentHandler;
import ntryn.alexa.service.MessagingService;
import ntryn.alexa.request.handler.intent.AmazonCancelIntentHandler;
import ntryn.alexa.request.handler.intent.AmazonHelpIntentHandler;
import ntryn.alexa.request.handler.intent.AmazonStopIntentHandler;
import ntryn.alexa.request.handler.LaunchRequestHandler;
import ntryn.alexa.request.handler.SessionEndedRequestHandler;
import ntryn.alexa.request.handler.SessionStartedRequestHandler;
import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.request.handler.intent.ControllerIntentHandler;
import com.amazon.speech.speechlet.services.DirectiveServiceClient;

/**
 * used to load resources
 * */
@Slf4j
class SkillContextLoader {
    /**
     * main loader method
     * */
    public static void load() {
        setLaunchAndSessionRequestHandlers();
        addIntentHandlers();
        loadMessagingService();
        loadDirectiveServiceClient();
        log.info("successfully loaded skill context");
    }

    /**
     * set Launch, SessionStarted and SessionEnded request Handlers
     * */
    private static void setLaunchAndSessionRequestHandlers() {
        log.info("loading Launch, SessionStarted and SessionEnded request handlers into skill context");
        SkillContext.context.setSessionStartedRequestHandler(new SessionStartedRequestHandler());
        SkillContext.context.setLaunchRequestRequestHandler(new LaunchRequestHandler());
        SkillContext.context.setSessionEndedRequestHandler(new SessionEndedRequestHandler());
    }

    /**
     * add new intent handler here
     * */
    private static void addIntentHandlers() {
        log.info("loading intent handler into skill Context.");
        SkillContext.context.addIntentHandler(AmazonHelpIntentHandler.class);
        SkillContext.context.addIntentHandler(AmazonCancelIntentHandler.class);
        SkillContext.context.addIntentHandler(AmazonStopIntentHandler.class);
        SkillContext.context.addIntentHandler(ControllerIntentHandler.class);
        SkillContext.context.addIntentHandler(ConfirmationIntentHandler.class);
    }

    /**
     * load messaging service
     * */
    private static void loadMessagingService() {
        SkillContext.context.setMessagingService(new MessagingService());
    }

    /**
     * load DirectiveServiceClient for progressive speech
     * */
    private static void loadDirectiveServiceClient() {
        SkillContext.context.setDirectiveServiceClient(new DirectiveServiceClient());
    }
}
