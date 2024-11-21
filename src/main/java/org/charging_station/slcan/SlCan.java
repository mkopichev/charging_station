package org.charging_station.slcan;

import org.charging_station.Charger;
import org.charging_station.SlCanListener;
import org.charging_station.serial.Serial;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SlCan {

    public static String byteArrayToString(Byte[] array) {
        byte[] bytes = new byte[array.length];
        int i = 0;
        for (byte b : array) {
            bytes[i++] = b;
        }

        return new String(bytes);
    }

    public static CanFrame slcanToFrame(byte[] byteData) {
        CanFrame result = new CanFrame();

        Byte[] slcanData = new Byte[byteData.length];
        for(int i = 0; i < byteData.length; i++) {
            slcanData[i] = byteData[i];
        }

        Byte type = slcanData[0];

        int id;
        int dlc;
        Byte[] idBytes;
        Byte[] dlcBytes;
        Byte[] dataBytes;
        if (type == 't') {
            // standard ID
            idBytes = Arrays.copyOfRange(slcanData, 1, 4);
            dlcBytes = Arrays.copyOfRange(slcanData, 4, 5);
            dataBytes = Arrays.copyOfRange(slcanData, 5, slcanData.length);
        } else if (type == 'T') {
            // extended ID
            idBytes = Arrays.copyOfRange(slcanData, 1, 9);
            dlcBytes = Arrays.copyOfRange(slcanData, 9, 10);
            dataBytes = Arrays.copyOfRange(slcanData, 10, slcanData.length);
        } else {
            // this isn't a valid frame
            return null;
        }
        String idString = byteArrayToString(idBytes);
        id = Integer.valueOf(idString, 16);
        result.setId(id);


        dlc = Integer.valueOf(byteArrayToString(dlcBytes));
        result.setDlc(dlc);

        byte[] data = {0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < dlc; i++) {
            String byteString;
            byteString = byteArrayToString(Arrays.copyOfRange(dataBytes,
                    i * 2, i * 2 + 2));
            data[i] = Byte.valueOf(byteString, 16);
        }
        result.setData(data);

        return result;
    }

    public static String frameToSlcan(CanFrame frame) {
        String result = "";

        result += "T";
        result += String.format("%08X", frame.getId());
        result += Integer.toString(frame.getDlc());

        for (int i : frame.getData()) {
            result += String.format("%02X", i & 0xFF);
        }

        result += "\r";

        return result;
    }

    public static void checkVersion(Serial serial, SlCanListener listener) {
        Thread thread = new Thread(() -> {
            serial.write("V\r");
            String version = serial.readLineBlocking(1000);
            if(version == null) {
                listener.errorHappened("Нет ответа от преобразователя в CAN");
                return;
            }
            listener.responseAcquired(SlCanListener.Request.VERSION_CHECK, "Версия преобразователя CAN - " + version);

        });
        thread.start();
    }

    public static void sendCommand(Serial serial, SlCanListener listener, Charger.ChargerCommand command, int value, Charger.ChargerOperation operation) {
        Thread thread = new Thread(() -> {
            if(operation == Charger.ChargerOperation.WRITE && !(command == Charger.ChargerCommand.START ||
                                                                command == Charger.ChargerCommand.STOP ||
                                                                command == Charger.ChargerCommand.VOLTAGE_SETPOINT ||
                                                                command == Charger.ChargerCommand.CURRENT_LIMIT ||
                                                                command == Charger.ChargerCommand.GROUP_ADDRESS ||
                                                                command == Charger.ChargerCommand.HI_LO_MODE_SET)) {
                listener.errorHappened("Нельзя записать эти данные");
                return;
            }

            if(operation == Charger.ChargerOperation.READ && (command == Charger.ChargerCommand.START ||
                                                              command == Charger.ChargerCommand.STOP ||
                                                              command == Charger.ChargerCommand.HI_LO_MODE_SET)) {
                listener.errorHappened("Нельзя прочитать эти данные");
                return;
            }

            CanFrame frame = new CanFrame();
            frame.setDlc(8);
            frame.setId(0x2200000); // Broadcast message

            byte[] data = new byte[8];
            switch (operation) {
                case READ -> data[0] = 2;
                case WRITE -> data[0] = 0;
            }
            data[1] = command.commandValue;
            switch (command) {
                case START -> data[7] = 0;
                case STOP -> data[7] = 1;
                case VOLTAGE_SETPOINT, CURRENT_LIMIT -> {
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    buffer.putInt(value);
                    for(int i = 0; i < 4; i++) {
                        data[4 + i] = buffer.get(i);
                    }
                }
                case GROUP_ADDRESS, HI_LO_MODE_SET -> {
                    listener.errorHappened("ХЗ что это за команда");
                    return;
                }

            }
            frame.setData(data);
            String slCanString = frameToSlcan(frame);
            serial.write(slCanString);
            String response = serial.readLineBlocking(1000);
            if(response == null) {
                listener.errorHappened("Нет ответа от преобразователя в CAN");
                return;
            }

            frame = slcanToFrame(response.getBytes(StandardCharsets.UTF_8));
            if (frame == null) {
                listener.errorHappened("Некорректный ответ");
                return;
            }

            byte[] responseData = frame.getData();

            if(responseData[1] != command.commandValue) {
                listener.errorHappened("Некорректный ответ");
                return;
            }

            if(command == Charger.ChargerCommand.START) {
                listener.responseAcquired(SlCanListener.Request.START, "");
                return;
            }

            if(command == Charger.ChargerCommand.STOP) {
                listener.responseAcquired(SlCanListener.Request.STOP, "");
                return;
            }

            if(operation == Charger.ChargerOperation.WRITE) {
                listener.responseAcquired(SlCanListener.Request.COMMAND, "Должно быть записано");
            }

            StringBuilder responseString = new StringBuilder(command.toString() + " = ");
            for(int i = 0; i < 4; i ++) {
                responseString.append(String.format("%02X", responseData[4 + i] & 0xFF));
            }

            listener.responseAcquired(SlCanListener.Request.COMMAND, responseString.toString());

        });
        thread.start();
    }

}
