package pong.gpio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public enum Pins {
    GPIO_00(RaspiPin.GPIO_15, 0, 0),
    GPIO_01(RaspiPin.GPIO_16, 1, 1),
    GPIO_02(RaspiPin.GPIO_08, 2, 2),
    GPIO_03(RaspiPin.GPIO_09, 3, 3),
    GPIO_04(RaspiPin.GPIO_07, 4, 4),
    GPIO_05(RaspiPin.GPIO_00, 9, 5),
    GPIO_06(RaspiPin.GPIO_01, 10, 6),
    GPIO_07(RaspiPin.GPIO_02, 11, 7),
    GPIO_08(RaspiPin.GPIO_03, 12, 8),
    GPIO_09(RaspiPin.GPIO_04, 13, 9),
    GPIO_10(RaspiPin.GPIO_05, 14, 10),
    GPIO_11(RaspiPin.GPIO_06, 15, 11),
    GPIO_12(RaspiPin.GPIO_12, 21, 12),
    GPIO_13(RaspiPin.GPIO_13, 22, 13),
    GPIO_14(RaspiPin.GPIO_14, 23, 14),
    GPIO_15(RaspiPin.GPIO_10, 24, 15),
    GPIO_16(RaspiPin.GPIO_11, 25, 16);

    private final Pin pin;
    private final int protoBoard;
    private final int logical;

    Pins(Pin pin, int protoBoard, int logical) {
        this.pin = pin;
        this.protoBoard = protoBoard;
        this.logical = logical;
    }

    public static Pins getByWiringPi(Pin pin) {
        for (Pins gpio : Pins.values()) {
            if (gpio.getWiringPi() == pin) {
                return gpio;
            }
        }
        throw new IllegalArgumentException("Pin not found");
    }

    public static Pins getByProtoBoard(int protoBoard) {
        for (Pins gpio : Pins.values()) {
            if (gpio.getProtoBoard() == protoBoard) {
                return gpio;
            }
        }
        throw new IllegalArgumentException("Pin not found");
    }

    public static Pins getByLogical(int logical) {
        for (Pins gpio : Pins.values()) {
            if (gpio.getLogical() == logical) {
                return gpio;
            }
        }
        throw new IllegalArgumentException("Pin not found");
    }

    public Pin getWiringPi() {
        return pin;
    }

    public int getProtoBoard() {
        return protoBoard;
    }

    public int getLogical() {
        return logical;
    }

    @Override
    public String toString() {
        return name() + "(" + pin.getAddress() + ", " + protoBoard + ", " + logical + ")";
    }
}
