package org.charging_station;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextField argumentValue;

    @FXML
    private GridPane background;

    @FXML
    private Button comConnectButton;

    @FXML
    private Text comConnectionStatus;

    @FXML
    private ComboBox<String> comPortChoice;

    @FXML
    private ComboBox<String> functionChoice;

    @FXML
    private TextField identifierValue;

    @FXML
    private ComboBox<String> readWriteChoice;

    @FXML
    private TextArea resultTextField;

    @FXML
    private ToggleButton startStopButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        readWriteChoice.getItems().removeAll(readWriteChoice.getItems());
        readWriteChoice.getItems().addAll("Чтение", "Запись");
        readWriteChoice.getSelectionModel().select("Выберите");
    }

    boolean start = false;

    @FXML
    void onStartStopButtonPress(ActionEvent event) {

        if (((ToggleButton) event.getSource()).getStyleClass().contains("start")) {
            ((ToggleButton) event.getSource()).getStyleClass().remove("start");
            ((ToggleButton) event.getSource()).getStyleClass().add("stop");
            ((ToggleButton) event.getSource()).setText("Стоп");
            start = true;
        } else {
            ((ToggleButton) event.getSource()).getStyleClass().remove("stop");
            ((ToggleButton) event.getSource()).getStyleClass().add("start");
            ((ToggleButton) event.getSource()).setText("Старт");
            start = false;
        }
    }
}