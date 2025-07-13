package ru.yandex.wordAccumulator.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.wordAccumulator.model.WordAccumulator;

//сериализатор для отправки объектов в JSON в Kafka.
public class JsonSerializer implements Serializer<WordAccumulator> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, WordAccumulator data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
