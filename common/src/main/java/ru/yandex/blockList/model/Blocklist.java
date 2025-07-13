package ru.yandex.blockList.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blocklist {
    private Integer user;
    private Set<Integer> blocklist;

    @Override
    public String toString() {
        return "Blocklist{" +
                "user=" + user +
                ", blocklist=" + blocklist +
                '}';
    }
}
