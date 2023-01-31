package com.github.postingbox.utils;

import java.time.LocalDate;

public class DateParseUtil {

    private DateParseUtil() {
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
