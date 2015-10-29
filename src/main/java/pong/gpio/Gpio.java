package pong.gpio;

public class Gpio extends Thread {
    static {
        System.loadLibrary("Gpio");
    }

    public static void main(String[] args) {
        Gpio gpio = new Gpio();
        gpio.start();
        System.out.println("Called");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private native void listen();

    private native void send(int value);

    private void receive(int value) {
        System.out.print(value);
    }

    @Override
    public void run() {
        listen();
    }
}
