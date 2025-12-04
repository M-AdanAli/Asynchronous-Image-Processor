package com.adanali.javafx.asynchronousimageprocessor.io;

import javafx.scene.image.WritableImage;

import java.util.Optional;

public interface CustomImageIO<T>{

    Optional<WritableImage> readImage(T src);

    void sendImage(WritableImage writableImage,T target);
}
