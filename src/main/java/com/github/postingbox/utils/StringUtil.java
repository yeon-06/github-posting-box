package com.github.postingbox.utils;

import java.io.UnsupportedEncodingException;

public class StringUtil {

	private static final String DEFAULT_ENCODING = "euc-kr";
	private static final String LINK_START_STRING = "http";
	private static final String LINK_END_STRING = " ";
	private static final String BLANK = " ";
	private static final int MAX_WORD_LENGTH = 9;

	private StringUtil() {
	}

	public static String substringByByte(int byteSize, String text) {
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

	public static String removeLink(String text) {
		// 띄어쓰기를 기준으로 단어를 구분할 때, http로 시작하는 단어는 제외한다.
		StringBuilder result = new StringBuilder(text);
		int linkStartIndex;
		while ((linkStartIndex = result.indexOf(LINK_START_STRING)) != -1) {
			int linkEndIndex = getLinkEndIndex(result, linkStartIndex);
			result.delete(linkStartIndex, linkEndIndex);
		}
		return result.toString();
	}

	private static int getLinkEndIndex(StringBuilder text, int linkStartIndex) {
		int index = text.substring(linkStartIndex).indexOf(LINK_END_STRING);
		if (index == -1) {
			return text.length();
		}
		return linkStartIndex + index;
	}

	public static String addSpaceInLongWord(String text) {
		String[] words = text.split(BLANK);
		StringBuilder result = new StringBuilder();
		for (String word : words) {
			result.append(generateWordWithBlank(word))
				.append(BLANK);
		}
		return result.toString();
	}

	private static String generateWordWithBlank(String word) {
		if (word.length() > MAX_WORD_LENGTH) {
			return word.substring(0, MAX_WORD_LENGTH) + BLANK + word.substring(MAX_WORD_LENGTH);
		}
		return word;
	}

	private static int getBytesLength(String text) {
		try {
			return text.getBytes(DEFAULT_ENCODING).length;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("유효하지 않은 인코딩 유형입니다.", e);
		}
	}
}
