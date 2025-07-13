package ru.yandex.censure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class СensureUtils {

    @Value("${spring.kafka.topic.censure}")
    private String TOPIC_NAME = "censure";

    private int WORD_INDEX = 1;
    private final StreamsBuilder builder = new StreamsBuilder();

    public void publishingCensure(KafkaTemplate<String, String> producer) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "badword", "goodword");

        try {
            producer.send(record);
        } catch (Exception e) {
            log.error("Ошибка при публикации допустимых синонимов запрещенных слов", e);
        }
    }

    public GlobalKTable<String, String> getСensureTable() {
        return builder.globalTable(TOPIC_NAME, Consumed.with(Serdes.String(), Serdes.String()));
    }
}
