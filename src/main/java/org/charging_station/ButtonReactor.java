package org.charging_station;

import java.util.ArrayList;

public interface ButtonReactor {

    void connectPressed(String com);
    void disconnectPressed();
    void startPressed();
    void stopPressed();
    void commandButtonPressed(Charger.ChargerCommand command, Charger.ChargerOperation operation, int argument);

    ArrayList<String> comPortsExpanded();

}
