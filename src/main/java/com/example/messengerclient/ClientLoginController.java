package com.example.messengerclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientLoginController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private double x = 0, y = 0;
    private ClientTransceiver clientTransceiver;

    @FXML
    private HBox top_bar;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Text error_text;

    @FXML
    public void loginButtonPressed(ActionEvent event) throws IOException {
        String username = username_field.getText();
        String password = password_field.getText();
        EventFlag status;

        clientTransceiver.sendLoginCredentials(username, password);
        System.out.println("Credentials sent!");
        status = clientTransceiver.waitForStatus();
        if (status.equals(EventFlag.VALID)) {
            System.out.println("Status is valid");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
            root = loader.load();

            ClientUIController clientUiController = loader.getController();
            clientUiController.passClientTransceiver(clientTransceiver);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            displayInvalidAlert(status.getMessage());
        }
    }

    @FXML
    public void createAccountButtonPressed(ActionEvent event) throws IOException {
        String username = username_field.getText();
        String password = password_field.getText();
        EventFlag status;

        clientTransceiver.sendNewAccountCredentials(username, password);
        System.out.println("Credentials sent!");
        status = clientTransceiver.waitForStatus();
        if (status.equals(EventFlag.VALID)) {
            System.out.println("Status is true");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-messenger.fxml"));
            root = loader.load();

            ClientUIController clientUiController = loader.getController();
            clientUiController.passClientTransceiver(clientTransceiver);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Messenger (" + username + ")");
            stage.show();
        } else {
            displayInvalidAlert(status.getMessage());
        }
    }
    @FXML
    public void minimizeApp(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.setIconified(true);
    }
    @FXML
    public void maximizeApp(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.setMaximized(!s.isMaximized());
    }
    @FXML
    public void closeApp(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        if (clientTransceiver != null) {
            clientTransceiver.shutdownClient();
        }
        s.close();
        System.exit(0);
    }

    private void displayInvalidAlert(String status) {
        System.out.println("Status is Invalid");
        error_text.setText(status);
        password_field.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        top_bar.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        top_bar.setOnMouseDragged(mouseEvent -> {
            ClientUI.globalstage.setX(mouseEvent.getScreenX() - x);
            ClientUI.globalstage.setY(mouseEvent.getScreenY() - y);
        });

        try {
            System.out.println("Building Controller");
            Socket socket = new Socket("localhost", 1234);
            clientTransceiver = new ClientTransceiver(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
