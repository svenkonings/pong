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
    private GuiBase gb;

    public BaseController(GuiBase gui_base) {
        gb = gui_base;
    }

    public void run() {
        test();
    }

    // Simulates pin handler updates
    public void test() {
        sleepAndCal(1000, 350);
//        sleepAndCal(1000, 350);
        sleepAndCal(1000, 1100);
//        sleepAndCal(1000, 1100);
//        sleepAndCal(1000, 1100);
        sleepAndCal(1000, 100);
//        sleepAndCal(1000, 100);
        for (int i = 0; i < 8; i++) {
            sleepAndY(100, 500, true);
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), false);
        }
        for (int i = 0; i < 5; i++) {
            sleepAndY(100, 500, true);
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), false);
        }
        for (int i = 0; i < 15; i++) {
            sleepAndY(100, 200, true);
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), false);
        }
        for (int i = 0; i < 30; i++) {
            sleepAndY(100, 500, true);
            sleepAndY(100, (int) (gb.getFieldHeight() * Math.random()), false);
        }
        for (int i = 0; i < 300; i++) {
            sleepAndY(10, i * 2, true);
            sleepAndY(10, 1080 - i * 2, false);
            sleepAndBall(10, i * 2, i * 2);
            sleepAndScore(10, true);
        }
        for (int i = 0; i < 30; i++) {
            sleepAndPause(100, i* 20);
        }
    }

    public void sleepAndCal(long ms, int coor) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.calibrateFpga(coor));
    }

    public void sleepAndY(long ms, int coor,boolean left) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (left) {
            Platform.runLater(() -> gb.updatePaddleLeft(coor));
        } else {
            Platform.runLater(() -> gb.updatePaddleRight(coor));
        }
    }

    public void sleepAndBall(long ms, int x, int y) {
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

    public void sleepAndScore(long ms, boolean left) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.updateGoal(left));
    }

    public void sleepAndPause(long ms, int y) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> gb.pause(y));
    }
}
