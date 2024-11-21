package org.charging_station.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Serial {

    private final SerialInterface serialInterface;

    public Serial(SerialInterface serialInterface) {
        this.serialInterface = serialInterface;
    }

    public enum BaudrateOptions {
        B4800("4800"),
        B9600("9600"),
        B19200("19200"),
        B38400("38400"),
        B57600("57600"),
        B74800("74800"),
        B115200("115200"),
        B230400("230400");

        public final String baudrate;

        BaudrateOptions(String baudrate) {

            this.baudrate = baudrate;
        }

        @Override
        public String toString() {
            return baudrate;
        }
    }

    public enum StopBitsOptions {
        ONE_STOP_BIT("1"),
        ONE_POINT_FIVE_STOP_BITS("1.5"),
        TWO_STOP_BITS("2");

        public final String stopBits;

        StopBitsOptions(String stopBits) {

            this.stopBits = stopBits;
        }

        @Override
        public String toString() {
            return stopBits;
        }
    }

    public enum ParityOptions {
        NO_PARITY("Выключен"),
        ODD_PARITY("Нечётность"),
        EVEN_PARITY("Чётность");

        public final String parity;

        ParityOptions(String parity) {

            this.parity = parity;
        }

        @Override
        public String toString() {
            return parity;
        }
    }

    public enum SerialPortState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }

    public static final int PORT_NAME_SHORT = 0;
    public static final int PORT_NAME_FULL = 1;

    /**
     * Returns all visible COM-ports as ArrayList of Strings
     *
     * @param  portNameType Selects if names are descriptive
     * @return              List of port names
     */
    public static ArrayList<String> checkPorts(int portNameType) {
        ArrayList<String> portNames = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port : ports) {
            if(portNameType == PORT_NAME_FULL)
                portNames.add(port.getDescriptivePortName());
            else if(portNameType == PORT_NAME_SHORT)
                portNames.add(port.getSystemPortName());
        }
        return portNames;
    }

    private int parity;
    private int stopBits = -1;
    private int baudrate = -1;
    private int dataBits = 8;
    private String portName;
    private SerialPort port;

    public SerialPortState getState() {
        return state;
    }

    private SerialPortState state = SerialPortState.DISCONNECTED;

    public void setParity(ParityOptions parity) {
        switch (parity) {
            case NO_PARITY -> this.parity = SerialPort.NO_PARITY;
            case ODD_PARITY -> this.parity = SerialPort.ODD_PARITY;
            case EVEN_PARITY -> this.parity = SerialPort.EVEN_PARITY;
            default -> this.parity = -1;
        }
    }

    public void setBaudrate(BaudrateOptions baudrate) {
        switch (baudrate) {
            case B4800 -> this.baudrate = 4800;
            case B9600 -> this.baudrate = 9600;
            case B19200 -> this.baudrate = 19200;
            case B38400 -> this.baudrate = 38400;
            case B57600 -> this.baudrate = 57600;
            case B74800 -> this.baudrate = 74800;
            case B115200 -> this.baudrate = 115200;
            case B230400 -> this.baudrate = 230400;
            default -> this.baudrate = -1;
        }
    }

    public void setStopBits(StopBitsOptions stopBits) {
        switch (stopBits) {
            case ONE_STOP_BIT -> this.stopBits = SerialPort.ONE_STOP_BIT;
            case ONE_POINT_FIVE_STOP_BITS -> this.stopBits = SerialPort.ONE_POINT_FIVE_STOP_BITS;
            case TWO_STOP_BITS -> this.stopBits = SerialPort.TWO_STOP_BITS;
        }
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int connect() {
        if(state == SerialPortState.CONNECTED)
            return 0;
        port = SerialPort.getCommPort(portName);
        port.setParity(parity);
        port.setBaudRate(baudrate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        if(port.openPort()) {
            state = SerialPortState.CONNECTED;
            if(serialInterface != null) {
                serialInterface.serialConnected();
            }
            return 0;
        }
        state = SerialPortState.DISCONNECTED;
        return -1;
    }

    public void disconnect() {
        if(state == SerialPortState.CONNECTED) {
            port.closePort();
            state = SerialPortState.DISCONNECTED;
        }
    }

    public int write(String data) {
        if(state != SerialPortState.CONNECTED)
            return -1;
        int num = port.writeBytes(data.getBytes(StandardCharsets.UTF_8), data.length());
        if (num != data.length())
            return -2;
        return 0;
    }

    public String readLineBlocking(long timeout) {
        long maxTime = System.currentTimeMillis() + timeout;
        byte[] buf = new byte[100];
        int i = 0;
        do {
            if (port.bytesAvailable() > 0) {
                port.readBytes(buf, 1, i++);
                if(buf[i-1] == '\r')
                    break;
            }
            if(System.currentTimeMillis() > maxTime)
                return null;
        } while (i < 100);
        if(buf[i-1] != '\r')
            return null;
        return new String(buf, StandardCharsets.UTF_8);
    }

}