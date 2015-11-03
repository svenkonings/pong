package pong.control;

import javafx.application.Platform;
import pong.gui.GuiBase;
import pong.gui.Paddle;

/* The great controller class:
    - Input from [communication]: the GPIO pin handler in C
    - Controls the [GUI]: in Java
    - Output to [communication]: the GPIO pin handler in C
 */
public class BaseController extends Thread {
    private static GuiBase gb;

    public BaseController(GuiBase gui_base) {
        gb = gui_base;
    }

    public void run() {
        test();
    }

    // Simulates pin handler updates
    public static void test() {
        sleepAndCal(100, 350);
//        sleepAndCal(1000, 350);
        sleepAndCal(100, 1100);
//        sleepAndCal(1000, 1100);
//        sleepAndCal(1000, 1100);
        sleepAndCal(100, 100);
//        sleepAndCal(1000, 100);
        for (int i = 0; i < 8; i++) {
            sleepAndY(100, 500, gb.getPaddleLeft());
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), gb.getPaddleRight());
        }
        for (int i = 0; i < 5; i++) {
            sleepAndY(100, 200, gb.getPaddleLeft());
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), gb.getPaddleRight());
        }
        for (int i = 0; i < 30; i++) {
            sleepAndY(100, 200, gb.getPaddleLeft());
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), gb.getPaddleRight());
        }
        for (int i = 0; i < 300; i++) {
            sleepAndY(20, i * 2, gb.getPaddleLeft());
            sleepAndY(20, 1080 - i * 2, gb.getPaddleRight());
            sleepAndBall(20, i * 2, i * 2);
            sleepAndScore(20, true);
        }
    }

    public static void sleepAndCal(long ms, int coor) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.calibrateFpga(coor));
    }

    public static void sleepAndY(long ms, int coor, Paddle paddle) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.updatePaddleY(coor, paddle));
    }

    public static void sleepAndBall(long ms, int x, int y) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            gb.updateBallX(x);
            gb.updateBallY(y);
        });
    }

    public static void sleepAndScore(long ms, boolean left) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.updateGoal(left));
    }
}
