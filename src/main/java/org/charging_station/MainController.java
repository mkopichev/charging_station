package org.charging_station;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GridPane background;

    @FXML
    private ComboBox<String> comBaudrateChoice;

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
    private TextArea resultTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        comBaudrateChoice.getItems().removeAll(comBaudrateChoice.getItems());
        comBaudrateChoice.getItems().addAll("2400", "4800", "9600", "19200", "38400", "57600", "74880", "115200", "230400", "230400");
        comBaudrateChoice.getSelectionModel().select("Бодрейт");

        functionChoice.getItems().removeAll(functionChoice.getItems());
        functionChoice.getItems().addAll("Читать", "Писать", "Старт/Стоп");
        functionChoice.getSelectionModel().select("Выберите");
    }
}