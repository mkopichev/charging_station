package org.charging_station;

import java.util.ArrayList;

public interface ButtonReactor {

    void connectPressed(String com);

    ArrayList<String> comPortsExpanded();

}
