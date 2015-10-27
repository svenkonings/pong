package pong.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import pong.Util;

public class GpioReceiveTest {
    public static void main(String[] args) {
        Pins[] pins = Pins.values();
        Util.inverse(pins);
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput[] inputs = new GpioPinDigitalInput[pins.length];
        for (int i = 0; i < pins.length; i++) {
            inputs[i] = gpio.provisionDigitalInputPin(pins[i].getWiringPi(), PinPullResistance.OFF);
        }
        GpioPinListenerDigital listener = event -> {
            if (event.getState() == PinState.HIGH) {
                System.out.println(GPIO.decimalState(GPIO.getStates(inputs, 2, inputs.length)));
            }
        };
        inputs[0].addListener(listener);
        System.out.println("Application started");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        gpio.shutdown();
        gpio.unprovisionPin(inputs);
        System.out.println("Pins shutdown");
    }
}
