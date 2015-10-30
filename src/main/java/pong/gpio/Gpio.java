package pong.gpio;

public class Gpio extends Thread {
    static {
        System.loadLibrary("Gpio");
    }

    private Listener listener;

    public Gpio(Listener listener) {
        this.listener = listener;
    }

    public static void main(String[] args) {
        Gpio gpio = new Gpio(new Listener() {
            @Override
            public void paddleLeft(int y) {
                System.out.println("paddleLeft: " + y);
            }

            @Override
            public void paddleRight(int y) {
                System.out.println("paddleRight: " + y);
            }

            @Override
            public void ballX(int x) {
                System.out.println("ballX: " + x);
            }

            @Override
            public void ballY(int y) {
                System.out.println("ballY: " + y);
            }

            @Override
            public void goalLeft() {
                System.out.println("goalLeft");
            }

            @Override
            public void goalRight() {
                System.out.println("goalRight");
            }

            @Override
            public void collision() {
                System.out.println("goalCollision");
            }
        });
        gpio.start();
        System.out.println("Called");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            gpio.send((int) (Math.random() * 32768)); // number between 0 and 2^15
        }
    }

    private native void listen();

    public native void send(int value);

    private void paddleLeft(int y) {
        listener.paddleLeft(y);
    }

    private void paddleRight(int y) {
        listener.paddleRight(y);
    }

    private void ballX(int x) {
        listener.ballX(x);
    }

    private void ballY(int y) {
        listener.ballY(y);
    }

    private void goalLeft() {
        listener.goalLeft();
    }

    private void goalRight() {
        listener.goalRight();
    }

    private void collision() {
        listener.collision();
    }

    @Override
    public void run() {
        listen();
    }

    public interface Listener {
        void paddleLeft(int y);

        void paddleRight(int y);

        void ballX(int x);

        void ballY(int y);

        void goalLeft();

        void goalRight();

        void collision();
    }
}
