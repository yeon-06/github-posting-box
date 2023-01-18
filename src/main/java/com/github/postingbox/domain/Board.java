package com.github.postingbox.domain;

import com.github.postingbox.support.DateParser;

import java.time.LocalDate;

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
        this.date = DateParser.parse(date);
    }

    public boolean isPostedDate(final LocalDate date) {
        return this.date.isEqual(date);
    }

    public void setResizedImageName(final String resizedImageName) {
        this.resizedImageName = resizedImageName;
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
