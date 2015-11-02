package pong.gpio;

public class Gpio extends Thread {
    public static final int CALIBRATION = 1;
    public static final int MENU = 2;
    public static final int START_GAME = 3;
    public static final int AI_1 = 4;
    public static final int AI_2 = 5;
    public static final int AI_3 = 6;
    public static final int PAUSE = 7;
    public static final int IDLE = 8;

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
            public void goalLeft() {
                System.out.println("goalLeft");
            }

            @Override
            public void paddleRight(int y) {
                System.out.println("paddleRight: " + y);
            }

            @Override
            public void goalRight() {
                System.out.println("goalRight");
            }

            @Override
            public void ballX(int x) {
                System.out.println("ballX: " + x);
            }

            @Override
            public void collision() {
                System.out.println("goalCollision");
            }

            @Override
            public void ballY(int y) {
                System.out.println("ballY: " + y);
            }

            @Override
            public void calibration(int value) {
                System.out.println("calibration: " + value);
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

    private void goalLeft() {
        listener.goalLeft();
    }

    private void paddleRight(int y) {
        listener.paddleRight(y);
    }

    private void goalRight() {
        listener.goalRight();
    }

    private void ballX(int x) {
        listener.ballX(x);
    }

    private void collision() {
        listener.collision();
    }

    private void ballY(int y) {
        listener.ballY(y);
    }

    private void calibration(int value) {
        listener.calibration(value);
    }

    @Override
    public void run() {
        listen();
    }

    public interface Listener {
        void paddleLeft(int y);

        void goalLeft();

        void paddleRight(int y);

        void goalRight();

        void ballX(int x);

        void collision();

        void ballY(int y);

        void calibration(int value);
    }
}
