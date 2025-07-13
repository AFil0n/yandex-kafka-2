package ru.yandex.wordContext.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import ru.yandex.wordContext.model.WordContext;

//сериализатор для отправки объектов в JSON в Kafka.
public class JsonSerializer implements Serializer<WordContext> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, WordContext data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
