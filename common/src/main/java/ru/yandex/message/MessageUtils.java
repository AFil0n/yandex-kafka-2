package ru.yandex.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.message.model.Message;
import ru.yandex.message.serialization.MessageSerdes;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class MessageUtils {

    private final Random random = new Random();
    private final StreamsBuilder builder = new StreamsBuilder();

    @Value("${spring.kafka.topic.message}")
    private String TOPIC_NAME = "message";

    public Message generateMessage() {
        Message message = Message.builder()
                .id(generateRandomID())
                .text(generateRandomString())
                .from(generateRandomInteger())
                .to(generateRandomInteger())
                .build();
        return message;
    }

    public Integer generateRandomInteger() {
        return random.nextInt(10) + 1;
    }

    public String generateRandomString() {
        return "Random text = " + (random.nextBoolean() ? "ordinaryword" : "badword");
    }

    public Long generateRandomID() {
        return random.nextLong(999999L);
    }

    public void publishingMessages(KafkaTemplate<String, Message> producer) {
        while (true) {
            try {
                Thread.sleep(60000L); // Ждем 60 секунд перед публикацией следующего сообщения
            } catch (InterruptedException e) {
                log.error("Ошибка при публикации сообщения (InterruptedException)", e);
                continue; // Прерываем цикл, если поток был прерван
            }

            var value = generateMessage();

            log.info("Публикуем сообщение: {}", value);

            ProducerRecord<String, Message> record = new ProducerRecord<>(
                    TOPIC_NAME, UUID.randomUUID().toString(), value);

            try {
                producer.send(record);
            } catch (Exception e) {
                log.error("Ошибка при публикации сообщения", e);
            }
        }
    }
}
