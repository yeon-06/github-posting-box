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

	public boolean containsDate(LocalDate date) {
		return value.stream()
			.anyMatch(it -> it.isPostedDate(date));
	}

	public List<Board> getValue() {
		return value;
	}

	public ImageSizeDto getImageSize() {
		return value.stream()
			.map(it -> ImageSizeDto.of(it.getImage()))
			.min(Comparator.comparingInt(ImageSizeDto::getHeight))
			.orElse(ImageSizeDto.of());
	}
}
