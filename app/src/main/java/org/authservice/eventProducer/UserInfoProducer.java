package org.authservice.eventProducer;

import com.nimbusds.jose.shaded.gson.JsonSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.authservice.model.UserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Properties;

@Service
public class UserInfoProducer {
    private final KafkaTemplate<String, UserInfoDTO> kafkaTemplate;

    @Value("${spring.kafka.topic-json.name}")
    private String topicJsonName;

    @Autowired
    UserInfoProducer(KafkaTemplate<String, UserInfoDTO> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoEvent eventData) {
        Message<UserInfoEvent> message = MessageBuilder.withPayload(eventData)
                .setHeader(KafkaHeaders.TOPIC, topicJsonName).build();
        kafkaTemplate.send(message);
    }
}