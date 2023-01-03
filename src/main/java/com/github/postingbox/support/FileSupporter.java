package com.github.postingbox.support;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class FileSupporter {

    public File resize(final String path, final String filePath, final int size) {
        try {
            BufferedImage bufferedImage = toBufferedImage(path);
            BufferedImage resizedBufferedImage = new BufferedImage(size, size, bufferedImage.getType());

            Graphics2D graphics = resizedBufferedImage.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, size, size, null);
            graphics.dispose();

            File file = new File(filePath);
            file.createNewFile();
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
