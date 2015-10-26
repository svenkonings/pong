package pong;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GpioReceiveTest {
    private static final int INPUT_PINS = 17;

    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput[] pins = new GpioPinDigitalInput[INPUT_PINS];
        for (int i = 0; i < INPUT_PINS; i++) {
            pins[i] = gpio.provisionDigitalInputPin(RaspiPin.getPinByName("GPIO " + i), PinPullResistance.OFF);
        }
        GpioPinListenerDigital listener = event -> System.out.println(event.getPin().getName() + ": " + event.getState());
        gpio.addListener(listener, pins);
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        gpio.shutdown();
        gpio.unprovisionPin(pins);
        System.out.println("Pins shutdown");
    }
}
