package ru.yandex.message.model;

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
public class Message {

    private Integer from; // Отправитель
    private Integer to; // Получатель

    private Long id; // Идентификатор сообщения
    private String text; // Текст сообщения

    @Override
    public String toString() {
        return String.format("id: %d :: text %s :: from %d :: to %d", id,  text, from, to);
    }
}
