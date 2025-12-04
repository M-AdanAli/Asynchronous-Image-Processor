package com.adanali.javafx.asynchronousimageprocessor.model;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GrayScaleImageFilter implements ImageFilter{
    @Override
    public WritableImage apply(WritableImage rgbImage) {
        if (rgbImage != null) {

            int width = (int) rgbImage.getWidth();
            int height = (int) rgbImage.getHeight();

            WritableImage grayscaleImage = new WritableImage(width, height);
            PixelReader pixelReader = rgbImage.getPixelReader();
            PixelWriter pixelWriter = grayscaleImage.getPixelWriter();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = pixelReader.getColor(x, y);

                    // Luminosity method for grayscale conversion
                    double grayValue = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();

                    // Create a new grayscale color with the calculated luminosity
                    Color grayColor = new Color(grayValue, grayValue, grayValue, color.getOpacity());
                    pixelWriter.setColor(x, y, grayColor);
                }
            }
            return grayscaleImage;
        } else throw new IllegalArgumentException("Please pass a valid Writable Image...");
    }
}
