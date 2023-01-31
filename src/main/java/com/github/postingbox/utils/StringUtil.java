package com.github.postingbox.utils;

import java.io.UnsupportedEncodingException;

public class StringUtil {

    private static final String DEFAULT_ENCODING = "euc-kr";

    private StringUtil() {
    }

    public static String substringByByte(final int byteSize, final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder newText = new StringBuilder();
        String nowChar;
        int nowBytesLength = 0;

        int length = text.length();
        for (int i = 0; i < length; i++) {
            nowChar = text.substring(i, i + 1);
            nowBytesLength += getBytesLength(nowChar);

            if (nowBytesLength > byteSize) {
                break;
            }

            newText.append(nowChar);
        }

        return newText.toString();
    }

    private static int getBytesLength(final String text) {
        try {
            return text.getBytes(DEFAULT_ENCODING).length;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("유효하지 않은 인코딩 유형입니다.", e);
        }
    }
}
