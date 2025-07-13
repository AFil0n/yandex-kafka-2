package ru.yandex.wordContext.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordContext {
    private Integer from;
    private Integer to;
    private Long messageId;
    private String replacement;
    private int order;
    private String original;

    @Override
    public String toString() {
        return "WordContext[" +
                "from=" + from + ", " +
                "to=" + to + ", " +
                "messageId=" + messageId + ", " +
                "replacement=" + replacement + ", " +
                "order=" + order + ']';
    }
}
