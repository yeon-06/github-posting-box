package com.github.postingbox.domain;

import com.github.postingbox.support.dto.ImageSizeDto;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class Boards {

	private final List<Board> value;

	public Boards(List<Board> value) {
		this.value = value;
	}

	public List<Board> getValue() {
		return value;
	}

	public ImageSizeDto getMinImageSize() {
		return value.stream()
			.map(it -> ImageSizeDto.of(it.getImage()))
			.min(Comparator.comparingInt(ImageSizeDto::getHeight))
			.orElse(ImageSizeDto.of());
	}

	public LocalDate getRecentPostDate() {
		return value.stream()
			.map(Board::getDate)
			.max(Comparator.comparing(LocalDate::toEpochDay))
			.orElseThrow(()-> new IllegalStateException("포스팅 목록에서 최신 날짜를 찾을 때 문제가 발생하였습니다."));
	}
}
