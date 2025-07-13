package ru.yandex.message.serialization;

import org.apache.kafka.common.serialization.Serdes;
import ru.yandex.message.model.Message;

public class MessageSerdes extends Serdes.WrapperSerde<Message> {
    public MessageSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
}
