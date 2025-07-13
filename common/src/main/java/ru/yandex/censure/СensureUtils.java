package ru.yandex.censure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class СensureUtils { //Класс для работы с цензурой
    private String TOPIC_NAME = "censure";

    // Запуск публикации маппинга слов
    public void publishingCensure(KafkaTemplate<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "badword", "goodword");

        try {
            producer.send(record);
        } catch (Exception e) {
            log.error("Ошибка при публикации допустимых синонимов запрещенных слов", e);
        }
    }
}
