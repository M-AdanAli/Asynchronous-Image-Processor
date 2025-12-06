# Asynchronous Image Processor ⚡🖼️

[![Language](https://img.shields.io/badge/Java-21-blue)](#)
[![Build](https://img.shields.io/badge/Build-Maven-red)](#)
[![UI](https://img.shields.io/badge/JavaFX-Enabled-green)](#)

A fast, asynchronous, tile-based image processing system built with **Java** and **JavaFX**.  
This project demonstrates a clean, scalable architecture for splitting large images (including 4K/8K), processing slices in parallel, and recombining them safely and efficiently.

Built for learning, experimentation, and extension.  
Simple where possible. Reliable where necessary. 🚀

---

## 🌟 Features at a glance

- 🔪 **Tile-based slicing** (recursive, balanced splits)
- 🧠 **Parallel processing** using `ForkJoinPool`
- 🎨 **Pluggable filters** (`ImageFilter` interface)
- 📨 **Decoupled processing and recomposition** via `BlockingQueue<ImageData>`
- 🧩 **Stable reassembly** with pixel-count validation
- 📷 **JavaFX image viewer** with smart auto-scaling for large images
- 💡 Designed for clarity, correctness, and extensibility

---

## 🧭 High-level Architecture

Client → ImageProcessingService → Fork/Join Tasks
→ (sliced tiles, processed)
→ BlockingQueue<ImageData>
→ ImageCombinerService → Completed Images

---

## 📂 Project Structure
```
│
├─ app/ → Application entrypoints (JavaFX startup)
├─ controller/ → UI controllers and image viewer handling
├─ io/ → Image loading / saving utilities
├─ model/ → Core data structures:
│ ImageData, ImageFilter, ImageProcessingTask
└─ service/ → Processing and recombination services
```
---

## 🧱 Key Components

### 📦 `model.ImageData`
Carries everything required for correct tile reassembly:

- `imageId`
- `WritableImage imageSlice`
- Absolute `(x, y)` placement
- Slice `width` and `height`
- `totalWidth` and `totalHeight`

No dependence on arrival order.  
No ambiguity in placement.

---

### ⚙️ `model.ImageProcessingTask`
A `RecursiveAction` that:

- Splits horizontally/vertically based on longest dimension
- Ensures integer-safe slicing (`mid = n/2`, `other = n - mid`)
- Applies the filter at leaf nodes
- Emits `ImageData` objects into a shared queue

Provides safe parallelism with minimal overhead.

---

### 🔄 `service.ImageCombinerService`
Consumes `ImageData` slices and reassembles them using:

- `ConcurrentHashMap<String, WritableImage>`
- Pixel-count tracking to detect completion
- Validation of slice boundaries
- Order-independent combination

Once assembled, images are placed into a final queue.

---

## 🖥️ JavaFX Image Viewer

Located in `controller/`.

Features:

- Displays any processed image
- Automatically scales down only oversized images
- Shows window controls even for 4K/8K outputs
- Leaves small images unscaled to avoid pixelation

---

## ▶️ Running the Project

### Build

```bash
mvn -ntp clean package
```
Launch
```java
Application.launch(MainApp.class, args);
```
Manual Invocation Example
```java
ForkJoinPool pool = new ForkJoinPool();
pool.invoke(new ImageProcessingTask(
    imageId,
    inputWritableImage,
    0, 0,
    width, height,
    new GrayscaleFilter(),
    sharedQueue
));
```
Collect completed images:
```java
WritableImage result = completedQueue.take();
```

🚧 Roadmap Ideas
- ⚡ GPU-accelerated tile compositing

- 🧵 Backpressure control for queue

- 🧪 Concurrency stress tests

- 📦 PixelBuffer-based recomposition

- 📊 Real-time progress UI

🤝 Contribution

Pull requests are welcome if they preserve:

- Deterministic recombination

- Clean slicing algorithm

- Clear layering between model/service/controller

Open an issue for bugs or improvements.

🎯 Final Notes

This project demonstrates a simple yet robust approach to asynchronous image slicing and recomposition.
Well-structured, extendable, and ideal for learning advanced concurrency concepts in Java.