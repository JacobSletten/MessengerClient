package com.example.messengerclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ClientUI extends Application {

    public static Stage globalstage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/messengerclient/client-login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        globalstage = stage;
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}