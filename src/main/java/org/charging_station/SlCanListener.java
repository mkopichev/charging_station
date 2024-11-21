package org.charging_station;

public interface SlCanListener {

    enum Request {
        VERSION_CHECK,
        START,
        STOP,
        WRITE_VOLTAGE,
        WRITE_CURRENT,
        READ_SOMETHING
    }

    void responseAcquired(Request request, String response);
    void errorHappened(String errorText);

}
