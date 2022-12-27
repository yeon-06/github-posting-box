package com.github.postingbox.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.Getter;

@Getter
public class Board {

    private final String title;
    private final LocalDate date;

    public Board(final String title, final String date) {
        this.title = title;
        this.date = toDate(date);
    }

    private LocalDate toDate(final String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. dd."));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. d."));
        }
    }

}
