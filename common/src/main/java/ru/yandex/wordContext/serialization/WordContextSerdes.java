package ru.yandex.wordContext.serialization;

import org.apache.kafka.common.serialization.Serdes;
import ru.yandex.wordContext.model.WordContext;

public class WordContextSerdes extends Serdes.WrapperSerde<WordContext> {
    public WordContextSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
}
