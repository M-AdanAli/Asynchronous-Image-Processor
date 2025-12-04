package com.adanali.javafx.asynchronousimageprocessor.model;

import javafx.scene.image.WritableImage;

public interface ImageFilter {

    WritableImage apply(WritableImage writableImage);

}
