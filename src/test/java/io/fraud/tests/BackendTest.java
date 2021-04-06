package io.fraud.tests;

import io.fraud.database.model.Deal;
import io.fraud.kafka.KafkaRecord;
import io.fraud.kafka.ProjectConfig;
import io.fraud.kafka.messages.Currency;
import io.fraud.kafka.messages.DealMessage;
import io.fraud.kafka.messages.ErrorMessage;
import io.fraud.kafka.messages.GeneratorMessage;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BackendTest extends BaseTest{

    // Kafka tests

    @Test
    void testCanWriteMessageToQueuingTransactions() {

        // Arrange
        String testMessage = "Test message from Java 8";
        kafkaService.subscribeTest();

        // Act
        kafkaService.send(ConfigFactory.create(ProjectConfig.class).testTopic(), testMessage);
        KafkaRecord receivedRecord = kafkaService.waitForMessage(testMessage);

        // Assert
        assertThat(receivedRecord).isNotNull();
    }

    @Test
    void testApplicationCanProcessValidMessage() {

        // Arrange
        kafkaService.subscribeLegit();

        // Act
        GeneratorMessage generatorMessage = kafkaService.send();
        DealMessage dealMessage = kafkaService.waitForMessage(generatorMessage.getSource()).valueAs(DealMessage.class);

        // Assert
        assertThat(dealMessage.getAmount()).isEqualTo(generatorMessage.getAmount());
        assertThat(dealMessage.getBaseCurrency()).isEqualTo(Currency.USD.toString());
    }

    @Test
    void testApplicationCanProcessFraudMessage() {

        // Arrange

        /*
        GeneratorMessage generatorMessage = kafkaService.send(new HashMap<String, String>() {{
            put("{{ faker.amount() }}", "2000");
        }});
        */

        // Two different types how to generate message (template engine or create class)
        // Can be parametrized (Streams JUnit) or using prepared data from files etc.

        GeneratorMessage generatorMessage = new GeneratorMessage();
        generatorMessage.setDate(new Date().toString());
        generatorMessage.setAmount(2000);
        generatorMessage.setCurrency(Currency.EUR.toString());
        generatorMessage.setSource(RandomStringUtils.randomAlphabetic(10));
        generatorMessage.setTarget(RandomStringUtils.randomAlphabetic(10));

        kafkaService.subscribeFraud();

        // Act
        kafkaService.send(generatorMessage);
        DealMessage dealMessage = kafkaService.waitForMessage(generatorMessage.getSource()).valueAs(DealMessage.class);

        // Assert
        assertThat(dealMessage.getAmount()).isEqualTo(2000);
        assertThat(dealMessage.getBaseCurrency()).isEqualTo(Currency.USD.toString());
    }

    @Test
    void testApplicationCanProcessErrorMessage() {

        // Arrange

        /*
        GeneratorMessage generatorMessage = kafkaService.send(new HashMap<String, String>() {{
            put("{{ faker.currency() }}", "2000");
        }});
        */

        // Two different types how to generate message (template engine or create class)
        // Can be parametrized (Streams JUnit) or using prepared data from files etc.

        GeneratorMessage generatorMessage = new GeneratorMessage();
        generatorMessage.setDate(new Date().toString());
        generatorMessage.setAmount(2050);
        generatorMessage.setCurrency("EA");
        generatorMessage.setSource(RandomStringUtils.randomAlphabetic(10));
        generatorMessage.setTarget(RandomStringUtils.randomAlphabetic(10));

        // Act
        kafkaService.subscribeError();
        kafkaService.send(generatorMessage);
        ErrorMessage errorMessage = kafkaService.waitForMessage(generatorMessage.getSource()).valueAs(ErrorMessage.class);

        // Assert
        assertThat(errorMessage.getErrors().stream().findFirst()).contains("Wrong currency code EA");
    }

    // Database tests

    @Test
    void testApplicationCanSaveFraudMessageToDatabase() {
        List<Deal> deal = dbService.findByCurrency(Currency.EUR.toString());

        assertThat(deal.size()).isEqualTo(7);
    }

    @Test
    void testApplicationCanSaveMessageToDatabase() {
        Deal deal = dbService.findDealById(3);

        assertThat(deal.getAmount()).isEqualTo(2000);
    }

    @Test
    void testApplicationCanSaveMessageToDatabase2() {
        dbService.deleteById(1);

        List<Deal> deal = dbService.findBySource("RwbyawhFDStH");

        assertThat(deal.size()).isEqualTo(0);
    }

    // End-to-End tests

    @Test
    void testApplicationPipeline() {

        // Arrange

        // Act
        GeneratorMessage generatorMessage = kafkaService.send();
        List<Deal> deals = dbService.findBySource(generatorMessage.getSource());

        // Assert
        assertThat(deals.size()).isEqualTo(1);
    }

    @Test
    void testApplicationPipelineWithRestAssured() {

        // Arrange

        // Act
        GeneratorMessage generatorMessage = kafkaService.send();
        List<Deal> deals = dbService.findBySource(generatorMessage.getSource());

        // Assert
        assertThat(deals.size()).isEqualTo(1);
        RestAssured
                .given()
                .when().get(ConfigFactory.create(ProjectConfig.class).dealsIdEndpoint(), deals.get(0).getId())
                .then().log().all().statusCode(200);
    }
}
