package org.charging_station;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.charging_station.serial.Serial;
import org.charging_station.serial.SerialInterface;
import org.charging_station.slcan.SlCan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainApplication extends Application implements SerialInterface, ButtonReactor, SlCanListener {

    MainController controller;
    Serial serial;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("sceneMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        controller = fxmlLoader.getController();
        controller.setReactor(this);
        stage.setScene(scene);
        stage.setMinHeight(768);
        stage.setMinWidth(1366);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setTitle("Приложение для зарядной станции");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("appIcon.png"))));
        stage.show();

        serial = new Serial(this);

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void serialConnected() {

    }

    @Override
    public void serialLost() {

    }

    @Override
    public void transmitRequest(int id, int command, int mode, int data) {

    }

    @Override
    public void connectPressed(String com) {

        if(com == null) {
            controller.log("Выберите COM-порт");
            controller.comChangedState(false);
            return;
        }
        serial.setPortName(com);
        serial.setBaudrate(Serial.BaudrateOptions.B115200);
        serial.setParity(Serial.ParityOptions.NO_PARITY);
        serial.setStopBits(Serial.StopBitsOptions.ONE_STOP_BIT);
        if(serial.connect() != 0) {
            controller.log("Ошибка подключения COM");
            controller.comChangedState(false);
            return;
        }
        controller.comChangedState(true);
        controller.log("Подключение к порту " + SerialPort.getCommPort(com).getDescriptivePortName() + " успешно");

        serial.write("C\r");
        serial.write("S4\r");
        serial.write("O\r");

        SlCan.checkVersion(serial, this);

    }

    @Override
    public void disconnectPressed() {
        serial.disconnect();
        controller.comChangedState(false);
        controller.log("COM порт отключен");
    }

    @Override
    public void startPressed() {
        if(serial.getState() == Serial.SerialPortState.DISCONNECTED) {
            controller.log("Не подключен COM порт");
            controller.startStateChanged(false);
            return;
        }
        SlCan.sendCommand(serial, this, Charger.ChargerCommand.START, 0, Charger.ChargerOperation.WRITE);
    }

    @Override
    public void stopPressed() {
        if(serial.getState() == Serial.SerialPortState.DISCONNECTED) {
            controller.log("Не подключен COM порт");
            controller.startStateChanged(false);
            return;
        }
        SlCan.sendCommand(serial, this, Charger.ChargerCommand.STOP, 1, Charger.ChargerOperation.WRITE);
    }

    @Override
    public void commandButtonPressed(Charger.ChargerCommand command, Charger.ChargerOperation operation, int argument) {
        if(serial.getState() == Serial.SerialPortState.DISCONNECTED) {
            controller.log("Не подключен COM порт");
            return;
        }
        SlCan.sendCommand(serial, this, command, argument, operation);
    }


    @Override
    public ArrayList<String> comPortsExpanded() {
        return Serial.checkPorts(Serial.PORT_NAME_SHORT);
    }

    @Override
    public void responseAcquired(Request request, String response) {
        switch (request) {
            case VERSION_CHECK -> controller.log(response);
            case COMMAND -> controller.log(response);
            case START -> {
                controller.log("Модуль включен");
                controller.startStateChanged(true);
            }
            case STOP -> {
                controller.log("Модуль выключен");
                controller.startStateChanged(false);
            }

        }
    }

    @Override
    public void errorHappened(String errorText) {
        controller.log(errorText);
    }
}