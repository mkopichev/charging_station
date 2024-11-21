package org.charging_station;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
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
    private ComboBox<Charger.CHARGER_COMMANDS> functionChoice;

    @FXML
    private TextField identifierValue;

    @FXML
    private ComboBox<String> readWriteChoice;

    @FXML
    private TextArea resultTextField;

    @FXML
    private ToggleButton startStopButton;


    private ButtonReactor reactor;

    public void setReactor(ButtonReactor reactor) {
        this.reactor = reactor;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        readWriteChoice.getItems().removeAll(readWriteChoice.getItems());
        readWriteChoice.getItems().addAll("Чтение", "Запись");
        readWriteChoice.getSelectionModel().select("Выберите");

        functionChoice.getItems().addAll(Charger.CHARGER_COMMANDS.values());

        comPortChoice.showingProperty().addListener((observableValue, wasShowing, isShowing) -> {
            if(isShowing) {
                ArrayList<String> vals = reactor.comPortsExpanded();
                String oldVal = comPortChoice.getValue();
                comPortChoice.getItems().clear();
                comPortChoice.getItems().addAll(vals);
                if(oldVal != null && vals.contains(oldVal)) {
                    comPortChoice.getSelectionModel().select(oldVal);
                }
            }
        });

        comConnectButton.setOnAction(actionEvent -> {
            reactor.connectPressed(comPortChoice.getValue());

        });
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

    public void log(String text) {
        resultTextField.appendText(text+"\n");
    }
}