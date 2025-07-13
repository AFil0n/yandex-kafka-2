package ru.yandex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.blockList.model.Blocklist;
import ru.yandex.blockList.BlockListUtils;

@SpringBootApplication
@EnableConfigurationProperties
public class BlockingListProducerApplication {
    @Autowired
    private KafkaTemplate<String, Blocklist> kafkaTemplate;

    public static void main(String[] args) {
        var context = SpringApplication.run(BlockingListProducerApplication.class, args);
        BlockingListProducerApplication app = context.getBean(BlockingListProducerApplication.class);
        new BlockListUtils().publishingBlockingList(app.kafkaTemplate);
    }
}