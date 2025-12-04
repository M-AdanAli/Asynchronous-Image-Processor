package com.adanali.javafx.asynchronousimageprocessor.model;

import javafx.scene.image.WritableImage;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RecursiveAction;

public class ImageProcessingTask extends RecursiveAction{
    private final String imageId;
    private final WritableImage slice;
    private final ImageFilter filter;
    private final BlockingQueue<ImageData> imageDataQueue;
    private final int x;
    private final int y;
    private final int totalWidth;
    private final int totalHeight;
    private static final int PIXEL_THRESHOLD = 50_000; // or width*height <= threshold

    public ImageProcessingTask(String imageId, WritableImage slice, int x, int y, int totalWidth, int totalHeight, ImageFilter filter, BlockingQueue<ImageData> imageDataQueue) {
        if (imageId!=null && !imageId.isBlank() && slice!=null && filter!=null && imageDataQueue !=null) {
            this.imageId = imageId;
            this.slice = slice;
            this.x = x;
            this.y = y;
            this.totalWidth = totalWidth;
            this.totalHeight = totalHeight;
            this.filter = filter;
            this.imageDataQueue = imageDataQueue;
        }else throw new IllegalArgumentException("Please pass valid arguments...");
    }

    @Override
    protected void compute(){
        int sliceHeight = (int) Math.round(this.slice.getHeight());
        int sliceWidth = (int) Math.round(this.slice.getWidth());

        if (sliceHeight*sliceWidth <= PIXEL_THRESHOLD){
            WritableImage processedSlice = filter.apply(slice);
            ImageData imageData = new ImageData(imageId,processedSlice,x,y,sliceWidth,sliceHeight,totalWidth,totalHeight);
            try {
                imageDataQueue.put(imageData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }

        boolean splitHorizontally = sliceWidth >= sliceHeight;

        if (splitHorizontally) {
            int mid = sliceWidth / 2;
            int rightWidth = sliceWidth - mid;

            WritableImage leftSlice =
                    new WritableImage(slice.getPixelReader(), 0, 0, mid, sliceHeight);
            ImageProcessingTask leftTask =
                    new ImageProcessingTask(imageId, leftSlice, x, y, totalWidth, totalHeight, filter, imageDataQueue);

            WritableImage rightSlice =
                    new WritableImage(slice.getPixelReader(), mid, 0, rightWidth, sliceHeight);
            ImageProcessingTask rightTask =
                    new ImageProcessingTask(imageId, rightSlice, x + mid, y, totalWidth, totalHeight, filter, imageDataQueue);

            leftTask.fork();
            rightTask.fork();
        }else {
            int mid = sliceHeight / 2;
            int bottomHeight = sliceHeight - mid;

            WritableImage topSlice =
                    new WritableImage(slice.getPixelReader(), 0, 0, sliceWidth, mid);
            ImageProcessingTask topTask =
                    new ImageProcessingTask(imageId, topSlice, x, y, totalWidth, totalHeight, filter, imageDataQueue);

            WritableImage bottomSlice =
                    new WritableImage(slice.getPixelReader(), 0, mid, sliceWidth, bottomHeight);
            ImageProcessingTask bottomTask =
                    new ImageProcessingTask(imageId, bottomSlice, x, y + mid, totalWidth, totalHeight, filter, imageDataQueue);

            topTask.fork();
            bottomTask.fork();
        }
    }
}
