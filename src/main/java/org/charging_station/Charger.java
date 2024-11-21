package org.charging_station;

public class Charger {

    public enum ChargerCommand {
        VOLTAGE_OUTPUT(0),
        CURRENT_OUTPUT(1),
        VOLTAGE_SETPOINT(2),
        CURRENT_LIMIT(3),
        SERIAL_NUMBER(5),
        STATUS_FLAG(8),
        LINE_VOLTAGE_AB(20),
        LINE_VOLTAGE_BC(21),
        LINE_VOLTAGE_CA(22),
        FAN_VOLTAGE_SETPOINT(26),
        TEMPERATURE(30),
        OUTPUT_CURRENT_FASTEST(47),
        OUTPUT_CURRENT_FAST(48),
        GROUP_ADDRESS(89),
        HI_LO_MODE_SET(95),
        HI_LO_MODE_STATUS(96),
        VOLTAGE_OUTPUT_FAST(98),
        HI_LO_MODE_REALTIME_STATUS(101),
        OUTPUT_CURRENT_CAPABILITY(104),
        OUTPUT_CURRENT_AND_CAPABILITY(114),
        START(4),
        STOP(4);

        public final byte commandValue;

        ChargerCommand(int commandValue) {
            this.commandValue = (byte)commandValue;
        }
    }

    public enum ChargerOperation{
        READ,
        WRITE
    }



}
