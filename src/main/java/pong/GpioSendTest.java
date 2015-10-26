package pong;

import com.pi4j.io.gpio.*;

public class GpioSendTest {
    private static final int OUTPUT_PINS = 17;

    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[OUTPUT_PINS];
        for (int i = 0; i < OUTPUT_PINS; i++) {
            pins[i] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + i), PinState.LOW);
            pins[i].setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        }
        if (args.length == 0) {
            for (GpioPinDigitalOutput pin : pins) {
                setStateWait(pin, true, 1000);
                setStateWait(pin, false, 1000);
            }
        } else {
            for (String arg : args) {
                setState(pins[Integer.parseInt(arg)], true);
            }
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        gpio.shutdown();
        gpio.unprovisionPin(pins);
        System.out.println("Pins shutdown");
    }

    private static void setState(GpioPinDigitalOutput pin, boolean state) {
        pin.setState(state);
        System.out.println(pin.getName() + " state: " + state);
    }

    private static void setStateWait(GpioPinDigitalOutput pin, boolean state, long sleep) {
        setState(pin, state);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
