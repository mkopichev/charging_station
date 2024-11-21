package org.charging_station;

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
    private ToggleButton comConnectButton;

    @FXML
    private Text comConnectionStatus;

    @FXML
    private ComboBox<String> comPortChoice;

    @FXML
    private ComboBox<Charger.ChargerCommand> functionChoice;

    @FXML
    private Button functionExecuteButton;

    @FXML
    private ComboBox<Charger.ChargerOperation> readWriteChoice;

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
        readWriteChoice.getItems().addAll(Charger.ChargerOperation.values());
        readWriteChoice.getSelectionModel().select(0);

        functionChoice.getItems().addAll(Charger.ChargerCommand.values());

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
            if(comConnectButton.isSelected())
                reactor.connectPressed(comPortChoice.getValue());
            else
                reactor.disconnectPressed();

        });

        startStopButton.setOnAction(actionEvent -> {
            if(startStopButton.isSelected()) {
                reactor.startPressed();
                startStopButton.setSelected(false);
            }
            else {
                reactor.stopPressed();
                startStopButton.setSelected(true);
            }
        });

        functionExecuteButton.setOnAction(actionEvent -> {
            if(functionChoice.getValue() == null) {
                log("Не выбрана комманда");
                return;
            }
            Integer argument = 0;
            if(readWriteChoice.getValue() == Charger.ChargerOperation.WRITE) {
                try {
                    argument = Integer.parseInt(argumentValue.getText());
                } catch (NumberFormatException e) {
                    log("Недопустимое значение аргумента");
                    return;
                }
                reactor.commandButtonPressed(functionChoice.getValue(), Charger.ChargerOperation.WRITE, argument);
                return;
            }

            if(readWriteChoice.getValue() == Charger.ChargerOperation.READ) {
                reactor.commandButtonPressed(functionChoice.getValue(), Charger.ChargerOperation.READ, 0);
                return;
            }
        });
    }


    void startStateChanged(boolean state) {
        if(state) {
            startStopButton.getStyleClass().remove("stop");
            startStopButton.getStyleClass().remove("start");
            startStopButton.getStyleClass().add("stop");
            startStopButton.setText("Стоп");
            startStopButton.setSelected(true);
        } else {
            startStopButton.getStyleClass().remove("stop");
            startStopButton.getStyleClass().remove("start");
            startStopButton.getStyleClass().add("start");
            startStopButton.setText("Старт");
            startStopButton.setSelected(false);
        }

    }

    public void log(String text) {
        resultTextField.appendText(text + "\n");
    }

    void comChangedState(boolean state) {
        if(state) {
            comConnectButton.getStyleClass().remove("stop");
            comConnectButton.getStyleClass().remove("start");
            comConnectButton.getStyleClass().add("start");
            comConnectButton.setText("Подключено");
            comConnectButton.setSelected(true);
            comPortChoice.setDisable(true);
        } else {
            comConnectButton.getStyleClass().remove("stop");
            comConnectButton.getStyleClass().remove("start");
            comConnectButton.getStyleClass().add("stop");
            comConnectButton.setText("Не подключено");
            comConnectButton.setSelected(false);
            comPortChoice.setDisable(false);
        }

    }

}