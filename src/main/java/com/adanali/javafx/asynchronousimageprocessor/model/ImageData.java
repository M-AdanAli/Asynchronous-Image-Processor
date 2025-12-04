package com.adanali.javafx.asynchronousimageprocessor.model;

import javafx.scene.image.WritableImage;

public class ImageData {

    public ImageData(String imageId, WritableImage imageSlice, int x, int y, int width, int height, int totalWidth, int totalHeight) {
        if (imageId!=null && !imageId.isBlank() && imageSlice!=null){
            this.imageId = imageId;
            this.imageSlice = imageSlice;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.totalWidth = totalWidth;
            this.totalHeight = totalHeight;
        }else throw new IllegalArgumentException("Please pass valid Arguments...");
    }

    public String getImageId() {
        return imageId;
    }

    public WritableImage getImageSlice() {
        return imageSlice;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    private String imageId;
    private WritableImage imageSlice;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int totalWidth;
    private final int totalHeight;

}
