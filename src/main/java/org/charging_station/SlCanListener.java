package org.charging_station;

public interface SlCanListener {

    enum Request {
        VERSION_CHECK,
        COMMAND,
        START,
        STOP
    }

    void responseAcquired(Request request, String response);
    void errorHappened(String errorText);

}
