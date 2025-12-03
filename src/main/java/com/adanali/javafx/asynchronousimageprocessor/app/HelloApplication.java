package com.adanali.javafx.asynchronousimageprocessor.app;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Hello World");
    }

}
