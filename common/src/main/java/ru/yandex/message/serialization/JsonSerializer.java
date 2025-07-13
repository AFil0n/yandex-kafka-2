package ru.yandex.message.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.yandex.message.model.Message;
import org.apache.kafka.common.serialization.Serializer;

//сериализатор для отправки объектов в JSON в Kafka.
public class JsonSerializer implements Serializer<Message> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Message data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации JSON", e);
        }
    }
}
