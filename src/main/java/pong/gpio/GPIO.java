package pong.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import pong.Util;

import java.util.Arrays;

public class GPIO {
    private static final int INPUT_PINS = 14;
    private static final int OUTPUT_PINS = 3;
    private static final int LISTEN_PINS = 2;

    private final GpioController gpio = GpioFactory.getInstance();
    private final GpioPinDigitalInput[] inputs = new GpioPinDigitalInput[INPUT_PINS];
    private final GpioPinDigitalOutput[] outputs = new GpioPinDigitalOutput[OUTPUT_PINS];

    public GPIO(Listener listener) {
        for (int i = 0; i < INPUT_PINS; i++) {
            inputs[i] = gpio.provisionDigitalInputPin(RaspiPin.getPinByName("GPIO " + i));
        }
        for (int i = 0; i < OUTPUT_PINS; i++) {
            outputs[i] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + (INPUT_PINS + i)));
        }
        GpioPinListenerDigital pinListener = event -> {
            Mode mode = Mode.getMode(decimalState(getStates(inputs, 0, LISTEN_PINS)));
            switch (mode) {
                case BALL_X:
                    listener.ballX(0);
                    break;
                case BALL_Y:
                    listener.ballY(0);
                    break;
                case PADDLE_LEFT:
                    listener.paddleLeft(0);
                    break;
                case PADDLE_RIGHT:
                    listener.paddleRight(0);
                    break;
            }
        };
        gpio.addListener(pinListener, Arrays.copyOf(inputs, LISTEN_PINS));
    }

    public static int decimalState(PinState[] states) {
        int value = 0;
        for (int i = states.length - 1; i >= 0; i--) {
            if (states[i].isHigh()) {
                value += Util.pow(2, i);
            }
        }
        return value;
    }

    public static PinState[] getStates(GpioPinDigital[] pins, int start, int end) {
        PinState[] states = new PinState[end - start];
        for (int i = start; i < end; i++) {
            states[i] = pins[i].getState();
        }
        return states;
    }

    private enum Mode {
        UNDEFINED(-1),
        BALL_X(0),
        BALL_Y(1),
        PADDLE_LEFT(2),
        PADDLE_RIGHT(3);

        private final int value;

        Mode(int value) {
            this.value = value;
        }

        public static Mode getMode(int value) {
            for (Mode mode : values()) {
                if (mode.equals(value)) {
                    return mode;
                }
            }
            return UNDEFINED;
        }

        public int getValue() {
            return value;
        }

        public boolean equals(int value) {
            return this.value == value;
        }
    }

    public interface Listener {
        void ballX(int x);

        void ballY(int y);

        void paddleLeft(int y);

        void paddleRight(int y);
    }
}
