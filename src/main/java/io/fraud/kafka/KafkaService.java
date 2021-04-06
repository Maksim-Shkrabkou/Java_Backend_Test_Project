package io.fraud.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fraud.kafka.consumer.KafkaMessageConsumer;
import io.fraud.kafka.messages.GeneratorMessage;
import io.fraud.kafka.producer.KafkaMessageProducer;
import lombok.SneakyThrows;
import org.aeonbits.owner.ConfigFactory;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.HashMap;

public class KafkaService {

    private final KafkaMessageProducer kafkaMessageProducer;
    private final KafkaMessageConsumer messageConsumer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProjectConfig projectConfig = ConfigFactory.create(ProjectConfig.class);
    private final TestDataGenerator testDataGenerator = new TestDataGenerator();

    public KafkaService() {
        this.kafkaMessageProducer = new KafkaMessageProducer(projectConfig.kafkaBrokers());
        this.messageConsumer = new KafkaMessageConsumer(projectConfig.kafkaBrokers());
    }

    public KafkaMessageConsumer getMessageConsumer() { return messageConsumer; }

    public RecordMetadata send(String message) { return send("test", message); }

    @SneakyThrows
    public GeneratorMessage send() {
        String message = testDataGenerator.generate("data/message.twig");
        send(projectConfig.queuingTopic(), message);

        return objectMapper.readValue(message, GeneratorMessage.class);
    }

    @SneakyThrows
    public GeneratorMessage send(HashMap<String, String> params) {
        String message = testDataGenerator.generate("data/message.twig", params);
        send(projectConfig.queuingTopic(), message);

        return objectMapper.readValue(message, GeneratorMessage.class);
    }

    @SneakyThrows
    public RecordMetadata send(Object message) {
        return send(projectConfig.queuingTopic(), objectMapper.writeValueAsString(message));
    }

    @SneakyThrows
    public RecordMetadata send(String topic, Object message) {
        return send(topic, objectMapper.writeValueAsString(message));
    }

    public RecordMetadata send(String topic, String message) { return kafkaMessageProducer.send(topic, message); }

    public void subscribeLegit() { subscribe(projectConfig.legitTopic()); }

    public void subscribeFraud() { subscribe(projectConfig.fraudTopic()); }

    public void subscribeError() { subscribe(projectConfig.errorTopic()); }

    public void subscribeTest() { subscribe(projectConfig.testTopic()); }

    public void subscribe(String topic) {
        messageConsumer.subscribe(topic);
        messageConsumer.consume();
    }

    public KafkaRecord waitForMessage(String message) { return messageConsumer.waitForMessage(message); }

}
