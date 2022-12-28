package com.github.postingbox.domain;

import java.time.LocalDate;
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

    public String generateContents(final String blogUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Board board : value) {
            stringBuilder.append(toDate(board))
                    .append("[")
                    .append(board.getTitle())
                    .append("](")
                    .append(blogUrl)
                    .append(board.getLink())
                    .append(")")
                    .append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("- (%d.%02d.%02d) ", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }
}
