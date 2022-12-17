package com.github.postingbox.domain;

import lombok.Getter;

@Getter
public class Board {

    private final String title;
    private final String date;

    public Board(final String title, final String date) {
        this.title = title;
        this.date = date;
    }
}
