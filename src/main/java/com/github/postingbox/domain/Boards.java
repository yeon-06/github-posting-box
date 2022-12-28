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

    public String generateContents() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Board board : value) {
            stringBuilder.append(newButton(board))
                    .append(toDate(board))
                    .append(board.getTitle())
                    .append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }


    private String newButton(final Board board) {
        if (board.getDate().isEqual(LocalDate.now())) {
            return "🆕 ";
        }
        return "";
    }

    private String toDate(final Board board) {
        LocalDate date = board.getDate();
        return String.format("(%d.%02d.%02d) ", date.getYear() % 100, date.getMonthValue(), date.getDayOfMonth());
    }
}
