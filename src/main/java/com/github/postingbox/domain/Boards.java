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
        stringBuilder.append("<table><tbody>");
        addTrTag(blogUrl, stringBuilder, 0, 3);
        addTrTag(blogUrl, stringBuilder, 3, value.size());
        stringBuilder.append("</tbody></table>");
        return stringBuilder.toString();
    }

    private void addTrTag(final String blogUrl, final StringBuilder stringBuilder, final int startIndex,
                          final int endIndex) {
        stringBuilder.append("<tr>");
        for (Board board : value.subList(startIndex, endIndex)) {
            stringBuilder.append("<td>")
                    .append("<a href=\"").append(blogUrl).append(board.getLink()).append("\">")
                    .append("<img width=\"100%\" src=\"https:").append(board.getImageUrl()).append("\"/><br/>")
                    .append("<div>").append(board.getTitle()).append("</div>")
                    .append("</a>")
                    .append("<div>").append(board.getSummary(), 0, 50).append("...</div>")
                    .append("<div>").append(toDate(board)).append("</div>")
                    .append("</td>");
        }
        stringBuilder.append("</tr>");
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("%d.%02d.%02d", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }
}
