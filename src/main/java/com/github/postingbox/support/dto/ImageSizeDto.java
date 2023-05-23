package com.github.postingbox.support.dto;

import java.awt.image.BufferedImage;

public class ImageSizeDto {

	private static final int MAX_WIDTH = 400;
	private static final int MAX_HEIGHT = 200;

	private final int height;
	private final int width;

	private ImageSizeDto(int height, int width) {
		this.height = height;
		this.width = width;
	}

	public static ImageSizeDto of(BufferedImage bufferedImage) {
		int height = bufferedImage.getHeight();
		int width = bufferedImage.getWidth();

		// width : height 비율을 2:1로 맞춘다
		if (width / 2 > height) {
			width = height * 2;
		} else {
			height = width / 2;
		}

		if (width > MAX_WIDTH) {
			width = MAX_WIDTH;
			height = MAX_HEIGHT;
		}

		return new ImageSizeDto(height, width);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
