package com.adanali.javafx.asynchronousimageprocessor.service;

import com.adanali.javafx.asynchronousimageprocessor.model.ImageData;
import com.adanali.javafx.asynchronousimageprocessor.model.ImageFilter;
import com.adanali.javafx.asynchronousimageprocessor.model.ImageProcessingTask;
import javafx.scene.image.WritableImage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum ImageProcessingService{

    INSTANCE();

    private final AtomicInteger imageCounter;
    private final BlockingQueue<ImageData> imageDataQueue;

    ImageProcessingService(){
        imageCounter = new AtomicInteger(0);
        imageDataQueue = new LinkedBlockingQueue<>();
    }

    public void processImage(WritableImage writableImage, ImageFilter imageFilter){
        try(ForkJoinPool executorService = new ForkJoinPool(Runtime.getRuntime().availableProcessors())){
            executorService.invoke(new ImageProcessingTask("img"+imageCounter.incrementAndGet(),writableImage,0,0,(int)writableImage.getWidth(),(int)writableImage.getHeight(),imageFilter,imageDataQueue));
        }
    }

    public BlockingQueue<ImageData> getImageDataQueue(){
        return this.imageDataQueue;
    }
}
