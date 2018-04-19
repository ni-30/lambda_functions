package ntryn.alexa.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Game;
import ntryn.alexa.common.Stage;
import ntryn.alexa.common.Utils;
import ntryn.alexa.dto.UserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQS queue service
 * */
@Slf4j
public class QueueService {
    private AmazonSQS awsSQS;

    public QueueService() {
        this.awsSQS = AmazonSQSClientBuilder
                              .standard()
                              .withRegion(AwsConfig.region)
                              .build();
    }

    public String createQueue(String uid) {
        CreateQueueRequest req = new CreateQueueRequest(getQueueNameFromUID(uid))
                .addAttributesEntry("DelaySeconds", "0")
                .addAttributesEntry("MaximumMessageSize", "1024")
                .addAttributesEntry("MessageRetentionPeriod", "1209600")
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "0")
                .addAttributesEntry("VisibilityTimeout", "30");
        CreateQueueResult result = awsSQS.createQueue(req);
        return result.getQueueUrl();
    }

    public boolean push(String tag, String uid, Map<String, Object> msg) {
        SendMessageRequest req = new SendMessageRequest()
                                                      .withQueueUrl(constructQueueUrlFromUID(uid))
                                                      .withMessageBody(Utils.serialize(msg))
                                                      .addMessageAttributesEntry("timestamp", new MessageAttributeValue().withDataType("Number").withStringValue(String.valueOf(System.currentTimeMillis())))
                                                      .addMessageAttributesEntry("tag", new MessageAttributeValue().withDataType("String").withStringValue(tag))
                                                      .withDelaySeconds(0);
        try {
            awsSQS.sendMessage(req);
            return true;
        } catch (AmazonSQSException e) {
            log.error("failed to push to SQS for source={}, uid={}, msg={}", tag, uid, msg, e);
        }

        return false;
    }

    public UserEntity pull(String uid, int waitTime) {
        final UserEntity entity = new UserEntity();
        entity.setUid(uid);

        final String queueUrl = constructQueueUrlFromUID(uid);

        List<Message> messages;
        do {
            try {
                ReceiveMessageRequest request = new ReceiveMessageRequest()
                                                        .withMessageAttributeNames("tag", "timestamp")
                                                        .withQueueUrl(queueUrl)
                                                        .withMaxNumberOfMessages(10)
                                                        .withWaitTimeSeconds(waitTime);
                messages = awsSQS.receiveMessage(request).getMessages();
            } catch (AmazonSQSException e) {
                log.error("unable to get message for uid={}", uid, e);
                return null;
            }

            messages.sort((m1, m2) -> {
                long t1 = Long.parseLong(m1.getMessageAttributes().get("timestamp").getStringValue());
                long t2 = Long.parseLong(m2.getMessageAttributes().get("timestamp").getStringValue());
                return t1 > t2 ? 1 : -1;
            });

            for (Message m : messages) {
                try {
                    String tag = m.getMessageAttributes().get("tag").getStringValue();

                    Map<String, Object> body = Utils.deserializeFromJsonString(m.getBody(), Map.class);

                    if (body.containsKey("emailId")) {
                        if ("final".equals(tag) && entity.getEmailId() != null) continue;
                        entity.setEmailId(body.get("emailId").toString());
                    }
                    if (body.containsKey("isConsoleLinked")) {
                        if ("final".equals(tag) && entity.getIsConsoleLinked() != null) continue;
                        entity.setIsConsoleLinked(Boolean.parseBoolean(body.get("isConsoleLinked").toString()));
                    }
                    if (body.containsKey("gcmEndpointArn")) {
                        if ("final".equals(tag) && entity.getGcmEndpointArn() != null) continue;
                        entity.setGcmEndpointArn(body.get("gcmEndpointArn").toString());
                    }
                    if (body.containsKey("stage")) {
                        entity.setStage(Stage.valueOf(body.get("stage").toString()));
                    }
                    if (body.containsKey("game")) {
                        entity.setGame(Game.valueOf(body.get("game").toString()));
                    }
                } finally {
                    awsSQS.deleteMessage(queueUrl, m.getReceiptHandle());
                }
            }
        } while (messages != null && !messages.isEmpty());

        return entity;
    }

    private String getQueueUrlFromUID(String uid) {
        GetQueueUrlRequest request = new GetQueueUrlRequest()
                                             .withQueueName(getQueueNameFromUID(uid))
                                             .withQueueOwnerAWSAccountId(AwsConfig.accountId);
        GetQueueUrlResult result;
        try {
            result = awsSQS.getQueueUrl(request);
        } catch (AmazonSQSException e) {
            if("AWS.SimpleQueueService.NonExistentQueue".equals(e.getErrorCode())) {
                try {
                    Thread.sleep(3000);
                } catch (Exception ignore) {}
                result = awsSQS.getQueueUrl(request);
            } else {
                throw e;
            }
        }

        if(result == null) {
            throw new AmazonSQSException("queue url not found");
        }

        return result.getQueueUrl();
    }

    private String constructQueueUrlFromUID(String uid) {
        return "https://sqs.us-east-1.amazonaws.com/" + AwsConfig.accountId + "/" + getQueueNameFromUID(uid);
    }

    private String getQueueNameFromUID(String uid) {
        return "fusion_play_uid-" + uid;
    }

}
