package com.github.postingbox.domain;

import java.util.List;

import lombok.Getter;

@Getter
public class Boards {

    private final List<Board> value;

    public Boards(final List<Board> value) {
        this.value = value;
    }

    public boolean containsToday() {
        return value.stream()
                .anyMatch(Board::isTodayPost);
    }
}
