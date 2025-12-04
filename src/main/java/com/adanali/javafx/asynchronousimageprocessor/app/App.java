package com.adanali.javafx.asynchronousimageprocessor.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Async Image Processor");
        stage.setScene(scene);
        stage.show();
    }

}
