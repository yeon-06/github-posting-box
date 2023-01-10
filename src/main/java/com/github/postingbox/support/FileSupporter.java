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

    public String findFileContent(final String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader.lines()
                    .collect(Collectors.joining(LINE_SEPARATOR));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] findFileContent(final File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File resizeAndSave(final String path, final String filePath, final int size) {
        try {
            BufferedImage bufferedImage = toBufferedImage(path);
            BufferedImage resizedBufferedImage = new BufferedImage(size, size, bufferedImage.getType());

            Graphics2D graphics = resizedBufferedImage.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, size, size, null);
            graphics.dispose();

            Path createdPath = Files.createFile(Paths.get(filePath));
            File file = new File(createdPath.toUri());
            ImageIO.write(resizedBufferedImage, "png", file);
            return file;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage toBufferedImage(final String path) {
        try {
            URL url = new URL(path);
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
