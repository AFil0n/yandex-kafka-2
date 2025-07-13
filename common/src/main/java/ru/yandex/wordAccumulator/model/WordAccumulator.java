package ru.yandex.wordAccumulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.message.model.Message;
import ru.yandex.wordContext.model.WordContext;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordAccumulator {
    private Integer from;
    private Integer to;
    private Long messageId;
    private final List<String> words = new ArrayList<>();

    @JsonIgnore
    public WordAccumulator addWord(WordContext context) {
        if (from == null) {
            from = context.getFrom();
        }

        if (to == null) {
            to = context.getTo();
        }

        if(messageId == null) {
            messageId = context.getMessageId();
        }

        // Добавляем слово и его позицию
        words.add(context.getReplacement() != null ? context.getReplacement() : context.getOriginal());

        return this;
    }

    @JsonIgnore
    public Message toMessage() {
        StringJoiner joiner = new StringJoiner(" ");

        for (String word : words) {
            joiner.add(word);
        }

        return Message.builder()
                .from(from)
                .to(to)
                .id(messageId)
                .text(joiner.toString())
                .build();
    }
}
