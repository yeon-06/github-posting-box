package com.github.postingbox.utils;

import com.github.postingbox.support.dto.ImageSizeDto;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

public class FileUtil {

	private static final String LINE_SEPARATOR = System.lineSeparator();

	private FileUtil() {
	}

	public static String findFileContent(String path) {
		FileReader fileReader = findFileReader(path);

		try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			return bufferedReader.lines()
				.collect(Collectors.joining(LINE_SEPARATOR));
		} catch (IOException e) {
			throw new IllegalArgumentException("BufferedReader를 읽을 수 없습니다.", e);
		}
	}

	public static byte[] findFileContent(File file) {
		try {
			return FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			throw new IllegalArgumentException("유효하지 않은 file 입니다.", e);
		}
	}

	public static File resize(BufferedImage bufferedImage, String filePath, ImageSizeDto imageSize) {
		try {
			BufferedImage resizedBufferedImage = new BufferedImage(imageSize.getWidth(), imageSize.getHeight(), bufferedImage.getType());

			Graphics2D graphics = resizedBufferedImage.createGraphics();
			graphics.drawImage(bufferedImage,
				0, 0, imageSize.getWidth(), imageSize.getHeight(),
				0, 0, imageSize.getWidth(), imageSize.getHeight(),
				null);
			graphics.dispose();

			File file = new File(filePath);
			ImageIO.write(resizedBufferedImage, "png", file);
			return file;

		} catch (IOException e) {
			String message = String.format("이미지 크기를 변경할 수 없습니다. -> filePath: %s", filePath);
			throw new IllegalArgumentException(message, e);
		}
	}

	private static FileReader findFileReader(String path) {
		try {
			return new FileReader(path);
		} catch (FileNotFoundException e) {
			String message = String.format("올바른 경로명을 입력하지 않아 파일을 찾을 수 없습니다 -> path: %s", path);
			throw new IllegalArgumentException(message, e);
		}
	}

	public static BufferedImage toBufferedImage(String path) {
		try {
			URL url = new URL(path);
			return ImageIO.read(url);
		} catch (IOException e) {
			String message = String.format("BufferedImage로 변환할 수 없습니다. -> path: %s", path);
			throw new IllegalArgumentException(message, e);
		}
	}
}
