package ru.yandex.wordAccumulator.serialization;

import org.apache.kafka.common.serialization.Serdes;
import ru.yandex.wordAccumulator.model.WordAccumulator;

public class WordAccumulatorSerdes extends Serdes.WrapperSerde<WordAccumulator> {
    public WordAccumulatorSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
}
