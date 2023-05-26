package com.github.postingbox.support.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ImageSizeDtoTest {

	private final int MAX_WIDTH = 400;
	private final int MAX_HEIGHT = 200;

	@DisplayName("인자 없이 생성 시 MAX_HEIGHTxMAX_WIDTH 크기이다.")
	@Test
	void of() {
		// given & when
		ImageSizeDto imageSizeDto = ImageSizeDto.of();

		// then
		assertEquals(MAX_HEIGHT, imageSizeDto.getHeight());
		assertEquals(MAX_WIDTH, imageSizeDto.getWidth());
	}

	@DisplayName("width가 최대 너비값 크고")
	@Nested
	class width_bigger_than_min {

		private final int width = MAX_WIDTH + 100;

		@DisplayName("height가 최대 높이보다 작은 경우, 크기는 height 기준으로 결정된다.")
		@Test
		void height_bigger_than_min() {
			// given
			int height = MAX_HEIGHT - 100;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(height * 2, imageSizeDto.getWidth());
			assertEquals(height, imageSizeDto.getHeight());
		}

		@DisplayName("height가 최대 높이와 동일한 경우, 크기는 height 기준으로 결정된다.")
		@Test
		void height_equals_to_min() {
			// given
			int height = MAX_HEIGHT;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(height * 2, imageSizeDto.getWidth());
			assertEquals(height, imageSizeDto.getHeight());
		}

		@DisplayName("height가 최대 높이보다 큰 경우, 크기는 최대값으로 결정된다.")
		@Test
		void height_smaller_than_min() {
			// given
			int height = MAX_HEIGHT + 100;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(MAX_WIDTH, imageSizeDto.getWidth());
			assertEquals(MAX_HEIGHT, imageSizeDto.getHeight());
		}
	}

	@DisplayName("width가 최대 너비값과 동일한 경우")
	@Nested
	class width_equals_to_min {

		private final int width = MAX_WIDTH;

		@DisplayName("height가 최대 높이보다 작은 경우, 크기는 height 기준으로 결정된다.")
		@Test
		void height_bigger_than_min() {
			// given
			int height = 100;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(height * 2, imageSizeDto.getWidth());
			assertEquals(height, imageSizeDto.getHeight());
		}

		@DisplayName("heigth가 최대 높이와 동일한 경우, 크기는 최대값으로 결정된다.")
		@Test
		void height_equals_to_min() {
			// given
			BufferedImage bufferedImage = new BufferedImage(width, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(MAX_WIDTH, imageSizeDto.getWidth());
			assertEquals(MAX_HEIGHT, imageSizeDto.getHeight());
		}

		@DisplayName("height가 최대 높이보다 큰 경우, 크기는 width 기준으로 결정된다.")
		@Test
		void height_smaller_than_min() {
			// given
			int height = 300;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(width, imageSizeDto.getWidth());
			assertEquals(width / 2, imageSizeDto.getHeight());
		}
	}

	@DisplayName("width가 최대 너비값보다 작은 경우")
	@Nested
	class width_smaller_than_min {

		private final int width = MAX_WIDTH - 100;

		@DisplayName("height가 최대 높이보다 작은 경우, 크기는 더 작은 값을 기준으로 결정된다.")
		@Test
		void height_bigger_than_min() {
			// given
			int height = MAX_HEIGHT - 10;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(width, imageSizeDto.getWidth());
			assertEquals(width / 2, imageSizeDto.getHeight());
		}

		@DisplayName("height가 최대 높이와 동일한 경우, 크기는 width 기준으로 결정된다.")
		@Test
		void height_equals_to_min() {
			// given
			BufferedImage bufferedImage = new BufferedImage(width, MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(width, imageSizeDto.getWidth());
			assertEquals(width / 2, imageSizeDto.getHeight());
		}

		@DisplayName("height가 최대 높이보다 큰 경우, 크기는 width 기준으로 결정된다.")
		@Test
		void height_smaller_than_min() {
			// given
			int height = MAX_HEIGHT + 100;
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			// when
			ImageSizeDto imageSizeDto = ImageSizeDto.of(bufferedImage);

			// then
			assertEquals(width, imageSizeDto.getWidth());
			assertEquals(width / 2, imageSizeDto.getHeight());
		}
	}
}
