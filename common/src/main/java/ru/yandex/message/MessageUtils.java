package ru.yandex.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.message.model.Message;

import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class MessageUtils { // Метод работы с сообщениями
    private final Random random = new Random();
    private String TOPIC_NAME = "message";

    //Публикация сообщений
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

    // Создание сообщения
    public Message generateMessage() {
        return Message.builder()
                .id(generateRandomID())
                .text(generateRandomString())
                .from(generateRandomInteger())
                .to(generateRandomInteger())
                .build();
    }

    //Генерация целого числа от 1 до 10
    public Integer generateRandomInteger() {
        return random.nextInt(10) + 1;
    }

    //Генерация строки
    public String generateRandomString() {
        return "Random text = " + (random.nextBoolean() ? "ordinaryword" : "badword");
    }

    //Генерация большого целого числа
    public Long generateRandomID() {
        return random.nextLong(999999L);
    }


}
