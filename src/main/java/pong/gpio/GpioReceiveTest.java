package pong.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import pong.Util;

import java.util.Arrays;

public class GpioReceiveTest {
    public static void main(String[] args) {
        Pins[] pins = Pins.values();
        Util.inverse(pins);
        System.out.println(Arrays.toString(pins));
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput[] inputs = new GpioPinDigitalInput[pins.length];
        for (int i = 0; i < pins.length; i++) {
            inputs[i] = gpio.provisionDigitalInputPin(pins[i].getWiringPi(), PinPullResistance.OFF);
        }
        GpioPinListenerDigital listener = event -> {
            if (event.getState() == PinState.HIGH) {
                GpioPinDigitalInput[] values = Arrays.copyOf(inputs, inputs.length);
                System.out.println(values[0].getState().getName() + ": " + GPIO.decimalState(GPIO.getStates(values, 2, values.length)));
            }
            System.out.flush();
        };
        inputs[0].addListener(listener);
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
