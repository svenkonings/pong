package pong;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

public class GPIOTest {
    private static final int OUTPUT_PINS = 17;

    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] outputs = new GpioPinDigitalOutput[OUTPUT_PINS];
        for (int i = 0; i < OUTPUT_PINS; i++) {
            outputs[i] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + i));
        }
        while (true) {
            for (GpioPinDigitalOutput output : outputs) {
                output.setState(output.getState().isLow());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
