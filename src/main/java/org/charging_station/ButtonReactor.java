package org.charging_station;

import java.util.ArrayList;

public interface ButtonReactor {

    void connectPressed(String com);
    void disconnectPressed();

    ArrayList<String> comPortsExpanded();

}
