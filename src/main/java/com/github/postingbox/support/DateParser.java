package com.github.postingbox.support;

import java.time.LocalDate;

public class DateParser {

    private DateParser() {
    }

    public static LocalDate parse(final String text) {
        String[] textArr = text.split("\\.");
        return LocalDate.of(
                toInt(textArr[0]),
                toInt(textArr[1]),
                toInt(textArr[2])
        );
    }

    private static int toInt(final String string) {
        return Integer.parseInt(string.strip());
    }
}
