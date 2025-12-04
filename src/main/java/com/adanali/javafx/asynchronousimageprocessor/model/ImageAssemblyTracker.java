package com.adanali.javafx.asynchronousimageprocessor.model;

import java.util.concurrent.atomic.AtomicLong;

public class ImageAssemblyTracker {
    private final long totalPixels;
    private final AtomicLong writtenPixels = new AtomicLong(0);

    public ImageAssemblyTracker(int totalWidth, int totalHeight) {
        this.totalPixels = (long) totalWidth * totalHeight;
    }

    public boolean addSlice(int w, int h) {
        long added = (long) w * h;
        long updated = writtenPixels.addAndGet(added);
        return updated == totalPixels;
    }
}
