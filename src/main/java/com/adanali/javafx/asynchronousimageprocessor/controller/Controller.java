package com.adanali.javafx.asynchronousimageprocessor.controller;

import com.adanali.javafx.asynchronousimageprocessor.model.GrayScaleImageFilter;
import com.adanali.javafx.asynchronousimageprocessor.model.ImageData;
import com.adanali.javafx.asynchronousimageprocessor.service.FileSystemImageIOService;
import com.adanali.javafx.asynchronousimageprocessor.service.ImageCombinerService;
import com.adanali.javafx.asynchronousimageprocessor.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        List<File> imagesPath = imageChooser.showOpenMultipleDialog(currentStage);
        try (ExecutorService executorService = Executors.newFixedThreadPool(imagesPath.size());){
            for (File imagePath: imagesPath){
                executorService.execute(()->{
                    Optional<WritableImage> writableImage = FileSystemImageIOService.INSTANCE.readImage(imagePath);
                    writableImage.ifPresent(this::startProcessing);
                });
            }
        }catch (NullPointerException | IllegalArgumentException e){
            System.err.println("Image not selected by the user");
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
        writableImage.ifPresent(img->Platform.runLater(()-> showImage(img)));
    }

    public void showImage(WritableImage image){
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            File saveLocation = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
            if (saveLocation != null){
                FileSystemImageIOService.INSTANCE.sendImage(image,saveLocation);
            }
        });

        double imgWidth = image.getWidth();
        double imgHeight = image.getHeight();

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double maxWidth = screen.getWidth() * 0.9;
        double maxHeight = screen.getHeight() * 0.9;

        if (imgWidth > maxWidth || imgHeight > maxHeight) {
            imageView.setFitWidth(maxWidth);
            imageView.setFitHeight(maxHeight);
        }


        HBox top = new HBox(saveButton);
        top.setSpacing(10);
        top.setPadding(new Insets(8));

        StackPane imagePane = new StackPane(imageView);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(imagePane);

        Scene scene = new Scene(root,maxWidth,maxHeight);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Image Viewer");
        stage.setMaxWidth(screen.getWidth());
        stage.setMaxHeight(screen.getHeight());
        stage.show();
    }

}
