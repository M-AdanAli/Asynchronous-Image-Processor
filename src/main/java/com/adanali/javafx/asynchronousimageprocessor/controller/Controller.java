package com.adanali.javafx.asynchronousimageprocessor.controller;

import com.adanali.javafx.asynchronousimageprocessor.model.GrayScaleImageFilter;
import com.adanali.javafx.asynchronousimageprocessor.model.ImageData;
import com.adanali.javafx.asynchronousimageprocessor.service.FileSystemImageIOService;
import com.adanali.javafx.asynchronousimageprocessor.service.ImageCombinerService;
import com.adanali.javafx.asynchronousimageprocessor.service.ImageProcessingService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

public class Controller implements Initializable {

    @FXML
    Button button;

    private FileChooser imageChooser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageChooser = new FileChooser();
        imageChooser.setTitle("Select the Image File");
        imageChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        imageChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Image Formats", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
    }

    public void selectImage(){
        Stage currentStage = (Stage) button.getScene().getWindow();
        try {
            File imagePath = imageChooser.showOpenDialog(currentStage);
            Optional<WritableImage> writableImage = new FileSystemImageIOService().readImage(imagePath);
            writableImage.ifPresent(this::startProcessing);
        }catch (IllegalArgumentException e){
            System.err.println("Image not selected by the User.");
        }

    }

    public void startProcessing(WritableImage writableImage){
        ImageProcessingService.INSTANCE.processImage(writableImage, new GrayScaleImageFilter());
        BlockingQueue<ImageData> imageDataQueue = ImageProcessingService.INSTANCE.getImageDataQueue();
        startCollecting(imageDataQueue);
    }

    public void startCollecting(BlockingQueue<ImageData> imageDataQueue){
        ImageCombinerService.INSTANCE.start(imageDataQueue);
        Optional<WritableImage> writableImage = ImageCombinerService.INSTANCE.pollCompletedImage();
        writableImage.ifPresent(this::showImage);
    }

    public void showImage(Image image){
        ImageView imageView = new ImageView(image);
        StackPane root = new StackPane();
        root.getChildren().add(imageView);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Image Viewer");
        stage.show();
    }

}
