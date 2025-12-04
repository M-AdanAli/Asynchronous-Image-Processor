package com.adanali.javafx.asynchronousimageprocessor.service;

import com.adanali.javafx.asynchronousimageprocessor.io.CustomImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileSystemImageIOService implements CustomImageIO<File> {

    /**
     * The "bufferedImage" can be null if no registered ImageReader claims to be able to read the resulting stream, in case of ImageIO.read(). While convertBufferedImageToWritableImage() require non-null, so in that case Null Pointer Exception can be thrown.
     * **/
    @Override
    public Optional<WritableImage> readImage(File src) {
        try {
            BufferedImage bufferedImage = ImageIO.read(src);
            return Optional.of(convertBufferedImageToWritableImage(bufferedImage));
        }catch (IOException | NullPointerException e){
            System.err.println("Some Error occurred while reading Image caused by : "+e.getClass()+"("+e.getMessage()+")");
        }
        return Optional.empty();
    }

    @Override
    public void sendImage(WritableImage writableImage, File target) {
        // Todo: implement this method to store the image to the file system
    }

    private static WritableImage convertBufferedImageToWritableImage(BufferedImage bufferedImage){
        return SwingFXUtils.toFXImage(bufferedImage,null);
    }
}
