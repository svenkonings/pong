package pong.gpio;

public class Gpio implements Runnable {
    static {
        System.loadLibrary("Gpio");
    }

    public static void main(String[] args) {
        new Thread(new Gpio()).start();
        System.out.println("Called");
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private native void listen();

    private native void send();

    private void receive(int value) {
        System.out.println(value);
    }

    @Override
    public void run() {
        listen();
    }
}
