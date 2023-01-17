package com.github.postingbox.domain;

import java.time.LocalDate;
import java.util.List;

public class Boards {

    private final List<Board> value;

    public Boards(final List<Board> value) {
        this.value = value;
    }

    public boolean containsDate(final LocalDate date) {
        return value.stream()
                .anyMatch(it -> it.isPostedDate(date));
    }

    public List<Board> getValue() {
        return value;
    }
}
