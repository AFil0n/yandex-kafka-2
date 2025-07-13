package ru.yandex.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig { // Загрузка имен и создание топиков
    @Value("${spring.kafka.topic.message}")
    private String messageTopic;

    @Value("${spring.kafka.topic.blocklist}")
    private String blockListTopic;

    @Value("${spring.kafka.topic.censure}")
    private String censureTopic;

    @Value("${spring.kafka.topic.filtered}")
    private String filteredTopic;

    @Bean
    public NewTopic messagesTopic() {
        return TopicBuilder.name(messageTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic blockListTopic() {
        return TopicBuilder.name(blockListTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic censureTopic() {
        return TopicBuilder.name(censureTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic filteredTopic() {
        return TopicBuilder.name(filteredTopic).partitions(1).replicas(1).compact().build();
    }
}
