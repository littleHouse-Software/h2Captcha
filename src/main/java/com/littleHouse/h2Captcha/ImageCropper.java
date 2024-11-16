package com.littleHouse.h2Captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageCropper {

    public static void saveCroppedImage(Bounds bounds, File inputFile, File outputFile) {
        try {
            BufferedImage bufferedInput = ImageIO.read(inputFile);
            BufferedImage bufferedCropped = bufferedInput.getSubimage(bounds.left, bounds.top, bounds.getWidth(), bounds.getHeight());
            BufferedImage bufferedOutput = new BufferedImage(bufferedCropped.getWidth(), bufferedCropped.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedOutput.createGraphics();
            graphics2D.drawImage(bufferedCropped, 0, 0, null);
            graphics2D.dispose();
            ImageIO.write(bufferedOutput, "jpg", outputFile);
        } catch (Exception e) {
            System.out.println("ImageCropper.saveCroppedImage: " + e);
        }
    }

}
