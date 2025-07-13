package ru.yandex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.censure.СensureUtils;

@SpringBootApplication
@EnableConfigurationProperties
public class CensureProducerApplacation {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public static void main(String[] args) {
        var context = SpringApplication.run(CensureProducerApplacation.class, args);
        CensureProducerApplacation app = context.getBean(CensureProducerApplacation.class);
        new СensureUtils().publishingCensure(app.kafkaTemplate);
    }
}