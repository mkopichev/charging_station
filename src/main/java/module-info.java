module org.acs.charging_station {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires com.fazecast.jSerialComm;
    requires java.desktop;


    opens org.charging_station to javafx.fxml;
    exports org.charging_station;
}