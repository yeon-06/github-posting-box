package com.github.postingbox.utils;

import java.time.LocalDate;

public class DateParseUtil {

    private DateParseUtil() {
    }

    public static LocalDate parse(final String text) {
        String regex = findRegex(text);
        String[] textArr = text.split(regex);
        return LocalDate.of(
                toInt(textArr[0]),
                toInt(textArr[1]),
                toInt(textArr[2])
        );
    }

    private static String findRegex(String text) {
        if(text.contains(".")) {
            return "\\.";
        }
        if(text.contains("-")) {
            return "-";
        }
        if(text.contains("/")) {
            return "/";
        }
        throw new IllegalArgumentException("지원하지 않는 형식의 날짜입니다.");
    }

    private static int toInt(final String string) {
        return Integer.parseInt(string.strip());
    }
}
