package com.adanali.javafx.asynchronousimageprocessor.service;

import com.adanali.javafx.asynchronousimageprocessor.io.CustomImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public enum FileSystemImageIOService implements CustomImageIO<File> {

    INSTANCE;
    /**
     * The "bufferedImage" can be null if no registered ImageReader claims to be able to read the resulting stream, in case of ImageIO.read(). While convertToWritableImage() require non-null, so in that case Null Pointer Exception can be thrown.
     * **/
    @Override
    public Optional<WritableImage> readImage(File src) {
        try {
            BufferedImage bufferedImage = ImageIO.read(src);
            return Optional.of(convertToWritableImage(bufferedImage));
        }catch (IOException | NullPointerException e){
            System.err.println("Some Error occurred while reading Image caused by : "+e.getClass()+"("+e.getMessage()+")");
        }
        return Optional.empty();
    }

    @Override
    public boolean sendImage(WritableImage writableImage, File target) {
        try {
            ImageIO.write(convertToBufferedImage(writableImage),"png",target);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Please pass valid Arguments");
        }catch (IOException e){
            System.err.println("Some error occurred while saving Image, caused by : "+e.getClass()+"("+e.getMessage()+")");
        }
        return false;
    }

    private static WritableImage convertToWritableImage(BufferedImage bufferedImage){
        return SwingFXUtils.toFXImage(bufferedImage,null);
    }

    private static BufferedImage convertToBufferedImage(WritableImage writableImage){
        return SwingFXUtils.fromFXImage(writableImage,null);
    }
}
