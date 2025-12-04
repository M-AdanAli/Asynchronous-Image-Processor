package com.adanali.javafx.asynchronousimageprocessor.service;

import com.adanali.javafx.asynchronousimageprocessor.model.ImageAssemblyTracker;
import com.adanali.javafx.asynchronousimageprocessor.model.ImageData;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public enum ImageCombinerService implements Runnable{

    INSTANCE;

    private BlockingQueue<ImageData> inputQueue;
    private BlockingQueue<WritableImage> completedImages = new LinkedBlockingQueue<>();
    private final Map<String, WritableImage> inProgressImages = new ConcurrentHashMap<>();
    private final Map<String, ImageAssemblyTracker> imageTrackers = new ConcurrentHashMap<>();

    private volatile boolean running = false;
    private Thread workerThread;

    public void start(BlockingQueue<ImageData> queue){
        if (!running){
            if (queue !=null){
                this.inputQueue = queue;
                running = true;
                workerThread = new Thread(this,"ImageCombinerThread");
                workerThread.setDaemon(true);
                workerThread.start();
            }else throw new IllegalArgumentException("Please pass a valid inputQueue");
        }
    }

    @Override
    public void run() {
        while (running){
            try {
                ImageData slice = inputQueue.take();
                combineSlice(slice);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void combineSlice(ImageData slice){
        String imageId = slice.getImageId();

        WritableImage fullImage = inProgressImages.computeIfAbsent(imageId,
                id-> new WritableImage(slice.getTotalWidth(), slice.getTotalHeight())
                );

        ImageAssemblyTracker imageTracker = imageTrackers.computeIfAbsent(imageId,
                id -> new ImageAssemblyTracker(slice.getTotalWidth(), slice.getTotalHeight())
                );

        PixelReader reader = slice.getImageSlice().getPixelReader();
        PixelWriter writer = fullImage.getPixelWriter();

        for (int dx = 0; dx < slice.getWidth(); dx++) {
            for (int dy = 0; dy < slice.getHeight(); dy++) {
                writer.setArgb(slice.getX() + dx, slice.getY() + dy, reader.getArgb(dx, dy));
            }
        }

        boolean finished = imageTracker.addSlice(slice.getWidth(), slice.getHeight());

        if (finished){
            inProgressImages.remove(imageId);
            imageTrackers.remove(imageId);
            completedImages.add(fullImage);
        }

        /*if (slice.getX() + slice.getWidth() >= slice.getTotalWidth() &&
                slice.getY() + slice.getHeight() >= slice.getTotalHeight()) {

            // Remove from in-progress and add to completed inputQueue
            inProgressImages.remove(imageId);
            completedImages.add(fullImage);
        }*/
    }

    public Optional<WritableImage> pollCompletedImage() {
        try {
            return Optional.ofNullable(completedImages.poll(1, TimeUnit.SECONDS));
        }catch (InterruptedException exception){
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

}
