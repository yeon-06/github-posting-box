package com.github.postingbox.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Board {

    private final String title;
    private final String link;
    private final String summary;
    private final String imageUrl;
    private final LocalDate date;
    private String resizedImageName;

    public Board(final String title, final String link, final String summary, final String imageUrl,
                 final String date) {
        this.title = title;
        this.link = link;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.date = toDate(date);
    }

    public boolean isTodayPost() {
        return date.isEqual(LocalDate.now());
    }

    public void setResizedImageName(final String resizedImageName) {
        this.resizedImageName = resizedImageName;
    }

    private LocalDate toDate(final String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. dd."));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy. MM. d."));
        }
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSummary() {
        return summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getResizedImageName() {
        return resizedImageName;
    }
}
