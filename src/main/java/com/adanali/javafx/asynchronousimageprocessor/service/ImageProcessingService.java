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
    private final Semaphore semaphore;

    ImageProcessingService(){
        imageCounter = new AtomicInteger(0);
        imageDataQueue = new LinkedBlockingQueue<>();
        semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());
    }

    public void processImage(WritableImage writableImage, ImageFilter imageFilter){
        try {
            semaphore.acquire();
            try(ForkJoinPool executorService = new ForkJoinPool(Runtime.getRuntime().availableProcessors())){
                executorService.invoke(new ImageProcessingTask("img"+imageCounter.incrementAndGet(),writableImage,0,0,(int)writableImage.getWidth(),(int)writableImage.getHeight(),imageFilter,imageDataQueue));
            }
            semaphore.release();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public BlockingQueue<ImageData> getImageDataQueue(){
        return this.imageDataQueue;
    }
}
