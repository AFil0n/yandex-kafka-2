package ru.yandex.blockList.serializer;

import org.apache.kafka.common.serialization.Serdes;
import ru.yandex.blockList.model.Blocklist;

public class BlocklistSerdes extends Serdes.WrapperSerde<Blocklist>{
    public BlocklistSerdes() {
        super(new JsonSerializer(), new JsonDeserializer());
    }
}
