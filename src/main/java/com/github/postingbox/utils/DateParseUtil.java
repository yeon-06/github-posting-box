package com.github.postingbox.utils;

import java.time.LocalDate;

public class DateParseUtil {

	private DateParseUtil() {
	}

	public static LocalDate parse(String text) {
		String regex = findRegex(text);
		String[] textArr = text.split(regex);
		return LocalDate.of(
			addYear(toInt(textArr[0])),
			toInt(textArr[1]),
			toInt(textArr[2])
		);
	}

	// TODO 연도 계산 코드 다시 짜기
	private static int addYear(int year) {
		return year < 2000 ? year + 2000 : year;
	}

	private static String findRegex(String text) {
		if (text.contains(".")) {
			return "\\.";
		}
		if (text.contains("-")) {
			return "-";
		}
		if (text.contains("/")) {
			return "/";
		}
		throw new IllegalArgumentException("지원하지 않는 형식의 날짜입니다.");
	}

	private static int toInt(String string) {
		return Integer.parseInt(string.strip());
	}
}
