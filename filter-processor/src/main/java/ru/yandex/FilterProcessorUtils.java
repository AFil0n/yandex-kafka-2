package ru.yandex;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.blockList.model.Blocklist;
import ru.yandex.blockList.serializer.BlocklistSerdes;
import ru.yandex.message.model.Message;
import ru.yandex.message.serialization.MessageSerdes;
import ru.yandex.wordAccumulator.model.WordAccumulator;
import ru.yandex.wordAccumulator.serialization.WordAccumulatorSerdes;
import ru.yandex.wordContext.model.WordContext;
import ru.yandex.wordContext.serialization.WordContextSerdes;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilterProcessorUtils {

    @Value("${spring.kafka.topic.filtered}")
    private String TOPIC_NAME = "filtered";

    public void runFilterProcessor() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "message-filter-processor");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker-0:9092,kafka-broker-1:9092,kafka-broker-2:9092");

        StreamsBuilder builder = new StreamsBuilder();

        // 1. Читаем GlobalKTable с блоклистом
        GlobalKTable<String, Blocklist> blocklistTable = builder.globalTable(
                "blocklist",
                Consumed.with(Serdes.String(), new BlocklistSerdes())
        );

        // 2. Читаем GlobalKTable с запрещёнными словами
        GlobalKTable<String, String> censureTable = builder.globalTable(
                "censure",
                Consumed.with(Serdes.String(), Serdes.String())
        );

        // 3. Поток сообщений
        KStream<String, Message> messageStream = builder.stream(
                "message",
                Consumed.with(Serdes.String(), new MessageSerdes())
        ).peek((key, message) ->
                log.info("📥 Received message: key={}, value={}", key, message.toString())
        );
        ;

        // 4. Фильтрация по блоклисту
        KStream<String, Message> filteredStream = messageStream
                .join(
                        blocklistTable,
                        (messageKey, message) -> {
                            log.info("🔍 Checking blocklist for key: {}", message.getFrom());
                            return String.valueOf(message.getFrom());
                        }, // ключ для поиска в блоклист
                        (message, blocklist) -> {
                            if (blocklist == null) {
                                log.info("✅ Message approved (no blocklist entry): {}", message.toString());
                                return message; // нет блоклиста — пропускаем
                            }

                            if (blocklist.getBlocklist().contains(message.getFrom())) {
                                log.warn("⛔ Message blocked: sender {} in blocklist", message.getFrom());
                                return null; // заблокирован — удаляем
                            }
                            log.info("✅ Message approved: {}", message);
                            return message;
                        }
                )
                .filter((key, message) -> {
                    log.debug("🗑️ Filtered out null message");
                    return message != null;
                });

        // 5. Разбивка на слова
        KStream<String, WordContext> wordsStream = filteredStream
                .peek((key, message) -> log.info("✂️ Splitting message into words: {}", message.getText()))
                .flatMap((key, message) -> {
                            List<KeyValue<String, WordContext>> splitWords = new ArrayList<>();
                            String[] wordArray = message.getText().split("\\s+");

                            for (int i = 0; i < wordArray.length; ++i) {
                                splitWords.add(KeyValue.pair(
                                        wordArray[i],
                                        WordContext.builder()
                                                .from(message.getFrom())
                                                .to(message.getTo())
                                                .messageId(message.getId())
                                                .order(i)
                                                .replacement(wordArray[i])
                                                .build()
                                ));
                            }

                            return splitWords;
                        }
                );

        // 6. Замена плохих слов через join с цензурной таблицей
        KStream<String, WordContext> censoredWordsStream = wordsStream
                .peek((word, context) -> log.info("🔠 Processing word: {}", word))
                .leftJoin(
                        censureTable,
                        (word, context) -> {
                            log.debug("🔎 Looking up word in censure table: {}", word);
                            return word;
                        },
                        (context, replacement) -> {
                            if (replacement != null) {
                                log.info("🚫 Censored word: {} -> {}", context.getReplacement(), replacement);

                                context.setReplacement(replacement);
                            }

                            log.info(context.getOriginal());
                            return context;
                        }
                );

        // 7. Группировка и сборка обратно в сообщения
        KStream<Long, Message> censoredMessages = censoredWordsStream
                .peek((word, context) -> log.info("🧩 Assembling message parts for ID: {}", context.getMessageId()))
                .groupBy(
                        (word, context) -> context.getMessageId(), // ключ - уникальный id сообщения
                        Grouped.with(Serdes.Long(), new WordContextSerdes())
                )
                .aggregate(
                        WordAccumulator::new,
                        (key, wordContext, accumulator) -> accumulator.addWord(wordContext),
                        Materialized.with(Serdes.Long(), new WordAccumulatorSerdes())
                )
                .toStream()
                .mapValues(WordAccumulator::toMessage);

        // 8. Вывод в финальный топик
        censoredMessages.peek((key, message) ->
                        log.info("✉️ Sending to output topic {}: key={}, value={}", TOPIC_NAME, key, message))
                .to(TOPIC_NAME, Produced.with(Serdes.Long(), new MessageSerdes()));

        // 9. Запуск приложения
        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Kafka Streams...");
            streams.close();
        }));
    }
}
