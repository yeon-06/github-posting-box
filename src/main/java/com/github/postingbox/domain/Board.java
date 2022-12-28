package com.github.postingbox.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.Getter;

@Getter
public class Board {

    private final String title;
    private final String link;
    private final LocalDate date;

    public Board(final String title, final String link, final String date) {
        this.title = title;
        this.link = link;
        this.date = toDate(date);
    }

    public boolean isTodayPost() {
        return date.isEqual(LocalDate.now());
    }

    private LocalDate toDate(final String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. dd."));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. d."));
        }
    }
}
