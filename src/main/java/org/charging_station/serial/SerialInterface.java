package org.charging_station.serial;

public interface SerialInterface {

    void serialConnected();

    void serialLost();

    void transmitRequest(int id, int command, int mode, int data);
}
