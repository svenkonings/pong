package pong;

import com.pi4j.io.gpio.*;

public class GPIOTest {
    private static final int OUTPUT_PINS = 17;

    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] pins = new GpioPinDigitalOutput[OUTPUT_PINS];
        for (int i = 0; i < OUTPUT_PINS; i++) {
            pins[i] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + i));
            pins[i].setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        }
        switch (args.length) {
            case 0:
                for (GpioPinDigitalOutput pin : pins) {
                    setState(pin, true, 1000);
                    setState(pin, false, 1000);
                }
                break;
            case 1:
                setState(pins[Integer.parseInt(args[0])], true, 60000);
                break;
            case 2:
                setState(pins[Integer.parseInt(args[0])], Boolean.parseBoolean(args[1]), 60000);
                break;
            default:
                System.out.println("Usage: [PIN NUMBER [STATE]]");
                break;
        }
        gpio.shutdown();
        gpio.unprovisionPin(pins);
        System.out.println("Pins shutdown");
    }

    private static void setState(GpioPinDigitalOutput pin, boolean state, long sleep) {
        pin.setState(state);
        System.out.println(pin.getName() + " state: " + state + " timeout: " + sleep);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
