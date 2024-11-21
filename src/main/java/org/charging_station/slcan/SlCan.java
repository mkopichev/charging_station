package org.charging_station.slcan;

import org.charging_station.SlCanListener;
import org.charging_station.serial.Serial;

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

        result += "t";
        result += String.format("%03X", frame.getId());
        result += Integer.toString(frame.getDlc());

        for (int i : frame.getData()) {
            result += String.format("%02X", i & 0xFF);
        }

        result += "\r";

        return result;
    }

    public static void checkVersion(Serial serial, SlCanListener listener) {
        Runnable runnable = () -> {
            serial.write("V\r");
            String version = serial.readLineBlocking(1000);
            if(version == null) {
                listener.errorHappened("Нет ответа от преобразователя в CAN");
                return;
            }
            listener.responseAcquired(SlCanListener.Request.VERSION_CHECK, "Версия преобразователя CAN - " + version);

        };
        runnable.run();
    }

}
