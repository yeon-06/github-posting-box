package com.github.postingbox.support;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileSupporter {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;

    public String findFileContent(final String path) {
        FileReader fileReader = findFileReader(path);

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines()
                    .collect(Collectors.joining(LINE_SEPARATOR));
        } catch (IOException e) {
            throw new IllegalArgumentException("BufferedReader를 읽을 수 없습니다.", e);
        }
    }

    public byte[] findFileContent(final File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new IllegalArgumentException("유효하지 않은 file 입니다.", e);
        }
    }

    public File resizeAndSave(final String path, final String filePath) {
        try {
            BufferedImage bufferedImage = toBufferedImage(path);
            BufferedImage resizedBufferedImage = new BufferedImage(WIDTH, HEIGHT, bufferedImage.getType());

            Graphics2D graphics = resizedBufferedImage.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, WIDTH, HEIGHT, null);
            graphics.dispose();

            Path createdPath = Files.createFile(Paths.get(filePath));
            File file = new File(createdPath.toUri());
            ImageIO.write(resizedBufferedImage, "png", file);
            return file;

        } catch (IOException e) {
            String message = String.format("파일의 이미지 크기를 변경할 수 없습니다. -> path: %s", path);
            throw new IllegalArgumentException(message, e);
        }
    }

    private FileReader findFileReader(final String path) {
        try {
            return new FileReader(path);
        } catch (FileNotFoundException e) {
            String message = String.format("올바른 경로명을 입력하지 않아 파일을 찾을 수 없습니다 -> path: %s", path);
            throw new IllegalArgumentException(message, e);
        }
    }

    private BufferedImage toBufferedImage(final String path) {
        try {
            URL url = new URL(path);
            return ImageIO.read(url);
        } catch (IOException e) {
            String message = String.format("BufferedImage로 변환할 수 없습니다. -> path: %s", path);
            throw new IllegalArgumentException(message, e);
        }
    }
}
