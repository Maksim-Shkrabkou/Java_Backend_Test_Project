package io.fraud.kafka;

import org.aeonbits.owner.Config;

import static org.aeonbits.owner.Config.*;

@Sources("classpath:config.properties")
public interface ProjectConfig extends Config {

    String env();

    @Key("${env}.dbHost")
    String dbHost();

    @Key("${env}.dbPort")
    int dbPort();

    @Key("${env}.dbName")
    String dbName();

    @Key("${env}.dbUser")
    String dbUser();

    @Key("${env}.dbPassword")
    String dbPassword();

    @Key("${env}.kafkaBrokers")
    String kafkaBrokers();

    @Key("${env}.dealsIdEndpoint")
    String dealsIdEndpoint();

    String legitTopic();

    String fraudTopic();

    String queuingTopic();

    String testTopic();

    String errorTopic();
}
