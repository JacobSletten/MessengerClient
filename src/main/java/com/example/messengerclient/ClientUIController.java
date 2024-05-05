package com.example.messengerclient;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientUIController implements Initializable {
    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private ClientTransceiver clientTransceiver;
    private final ExtractionFunction getData = messages::add;
    private double x = 0, y = 0;

    @FXML
    private TextField message_field;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private Text header_text;
    @FXML
    private HBox top_bar;

    public void passClientTransceiver(ClientTransceiver transceiver) {
        System.out.println("Passing Transceiver");
        this.clientTransceiver = transceiver;
        clientTransceiver.receiveMessageWithHook(getData);
    }

    @FXML
    public void sendButtonPressed(ActionEvent event) {
        String message = message_field.getText();
        clientTransceiver.sendMessage(message);
        message_field.clear();
    }
    @FXML
    public void enterKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            String message = message_field.getText();
            clientTransceiver.sendMessage(message);
            message_field.clear();
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
        s.setFullScreen(true);
    }
    @FXML
    public void closeApp(MouseEvent event) {
        Stage s = (Stage) ((Node)event.getSource()).getScene().getWindow();
        s.close();
        System.exit(0);
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

        messages.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Text text = new Text(messages.get(messages.size() - 1));
                        TextFlow textFlow = new TextFlow(text);
                        HBox hbox = new HBox();
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setPadding(new Insets(2,2,2,2));
                        hbox.getChildren().add(textFlow);
                        vbox_messages.getChildren().add(hbox);

                        header_text.setText(clientTransceiver.getClientUsername());

                        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
                            @Override
                            public void changed(ObservableValue<? extends Number>
                                                        observableValue, Number oldValue, Number newValue) {
                                sp_main.setVvalue((Double) newValue);
                            }
                        });
                    }
                });
            }
        });
    }
}