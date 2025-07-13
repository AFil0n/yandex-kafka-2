package ru.yandex.wordAccumulator.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.wordAccumulator.model.WordAccumulator;

import java.nio.charset.StandardCharsets;

// Десериализатор для получения объектов из JSON в Kafka
public class JsonDeserializer implements Deserializer<WordAccumulator> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public WordAccumulator deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }

            return OBJECT_MAPPER.readValue(new String(data, StandardCharsets.UTF_8), WordAccumulator.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации JSON", e);
        }
    }
}
