package ru.yandex.blockList;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.blockList.model.Blocklist;
import ru.yandex.blockList.serializer.BlocklistSerdes;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
@Component
public class BlockListUtils { //Класс для работы с Blocklist
    private final Random random = new Random();

    @Value("${spring.kafka.topic.blocklist}")
    private String TOPIC_NAME = "blocklist";

    public void publishingBlockingList(KafkaTemplate<String, Blocklist> producer) {
        for (int i = 1; i <= 10; i++){
            try {
                Thread.sleep(1000L); // Ждем 1 секунду перед публикацией следующего сообщения
            } catch (InterruptedException e) {
                log.error("Ошибка при публикации сообщения (InterruptedException)", e);
                continue; // Прерываем цикл, если поток был прерван
            }

            Blocklist bl = generateBlockList(i);

            try {
                producer.send(new ProducerRecord<>(TOPIC_NAME, bl.getUser().toString(), bl));
            } catch (Exception e) {
                log.error("Ошибка при публикации списка заблокированных пользователей", e);
            }
        }
    }

    private Blocklist generateBlockList(Integer userId) {
        return Blocklist.builder()
                .user(userId)
                .blocklist(generateIntegerSet())
                .build();
    }

    private Set<Integer> generateIntegerSet() {
        Integer n = random.nextInt(5) + 1;
        Set<Integer> res = new HashSet<>();

        for (int i = 0; i < n; i++) {
            res.add(generateInteger());
        }

        return res;
    }

    private Integer generateInteger() {
        return random.nextInt(10) + 1;
    }
}
