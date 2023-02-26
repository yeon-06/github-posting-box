package com.github.postingbox.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DateParseUtilTest {

	private static Stream<Arguments> parameters() {
		return Stream.of(
			Arguments.arguments("2023-02-26", LocalDate.of(2023, 2, 26)),
			Arguments.arguments("2023/02/26", LocalDate.of(2023, 2, 26)),
			Arguments.arguments("2023.02.26", LocalDate.of(2023, 2, 26)),
			Arguments.arguments("2023.2.26", LocalDate.of(2023, 2, 26)),
			Arguments.arguments("2023.10.06", LocalDate.of(2023, 10, 6)),
			Arguments.arguments("2023.10.6", LocalDate.of(2023, 10, 6))
		);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void parse(String value, LocalDate expected) {
		LocalDate result = DateParseUtil.parse(value);
		assertThat(result).isEqualTo(expected);
	}
}