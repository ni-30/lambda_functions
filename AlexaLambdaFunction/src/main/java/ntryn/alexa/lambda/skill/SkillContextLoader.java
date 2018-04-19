package ntryn.alexa.lambda.skill;

import ntryn.alexa.request.handler.ConfirmationIntentHandler;
import ntryn.alexa.request.handler.HowToPlayIntentHandler;
import ntryn.alexa.request.handler.ShutdownIntentHandler;
import ntryn.alexa.service.MessagingService;
import ntryn.alexa.request.handler.AmazonCancelIntentHandler;
import ntryn.alexa.request.handler.AmazonHelpIntentHandler;
import ntryn.alexa.request.handler.AmazonStopIntentHandler;
import ntryn.alexa.request.handler.LaunchRequestHandler;
import ntryn.alexa.request.handler.SessionEndedRequestHandler;
import ntryn.alexa.request.handler.SessionStartedRequestHandler;
import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.request.handler.ControllerIntentHandler;
import ntryn.alexa.service.QueueService;

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
        loadMessagingAndQueueServices();
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
        SkillContext.context.addIntentHandler(ShutdownIntentHandler.class);
        SkillContext.context.addIntentHandler(HowToPlayIntentHandler.class);
    }

    /**
     * load messaging service
     * */
    private static void loadMessagingAndQueueServices() {
        SkillContext.context.setMessagingService(new MessagingService());
        SkillContext.context.setQueueService(new QueueService());
    }

    /**
     * load DirectiveServiceClient for progressive speech
     * */
    private static void loadDirectiveServiceClient() {
        SkillContext.context.setDirectiveServiceClient(new DirectiveServiceClient());
    }
}
