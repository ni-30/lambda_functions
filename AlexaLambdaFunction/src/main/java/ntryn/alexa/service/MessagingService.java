package ntryn.alexa.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import lombok.Data;
import ntryn.alexa.AwsConfig;
import ntryn.alexa.common.Utils;
import ntryn.alexa.dto.GcmMsg;

import java.util.Map;

public class MessagingService {
    private AmazonSNS amazonSNS;

    public MessagingService() {
        this.amazonSNS = AmazonSNSClient.builder()
                                        .withRegion(AwsConfig.region)
                                        .build();
    }

    public String createPlatformEndpoint(String platformArn, String token, PlatformUserData userData) {
        CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();
        request.setPlatformApplicationArn(platformArn);
        request.setToken(token);
        request.setCustomUserData(Utils.serialize(userData));
        CreatePlatformEndpointResult result = this.amazonSNS.createPlatformEndpoint(request);
        return result.getEndpointArn();
    }

    public void publishWithTargetArn(String endpointArn, GcmMsg message) {
        this.amazonSNS.publish(new PublishRequest()
                                       .withTargetArn(endpointArn)
                                       .withMessage(message.toGcmMsg())
                                       .withMessageStructure("json"));
    }

    public void publishWithTargetArn(String endpointArn, Map<String, Object> message) {
        this.amazonSNS.publish(new PublishRequest()
                                       .withTargetArn(endpointArn)
                                       .withMessage(Utils.serialize(message))
                                       .withMessageStructure("json"));
    }

    public void publishWithTopicArn(String topicArn, GcmMsg message) {
        this.amazonSNS.publish(new PublishRequest()
                                       .withTopicArn(topicArn)
                                       .withMessage(message.toGcmMsg())
                                       .withMessageStructure("json"));
    }

    public String subscribeTopic(String topicArn, String protocol, String endpointArn) {
        SubscribeRequest request = new SubscribeRequest()
                                           .withTopicArn(topicArn)
                                           .withProtocol(protocol)
                                           .withEndpoint(endpointArn);
        SubscribeResult result = this.amazonSNS.subscribe(request);
        return result.getSubscriptionArn();
    }

    public void unsubscribeTopic(String subscriptionArn) {
        this.amazonSNS.unsubscribe(subscriptionArn);
    }

    @Data
    public static class PlatformUserData {

    }
}
