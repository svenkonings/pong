package pong.control;

import javafx.application.Platform;
import pong.gui.GUI;
import pong.gui.GuiBase;

/**
 * Created by Lindsay on 29-Oct-15.
 */
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
        sleepAndCal(1000, 350);
        sleepAndCal(1000, 1100);
        sleepAndCal(1000, 100);
        for (int i = 0; i < 8; i++) {
            sleepAndY(100, 500);
        }
        for (int i = 0; i < 5; i++) {
            sleepAndY(100, 200);
        }
        for (int i = 0; i < 30; i++) {
            sleepAndY(200, 500);
        }
    }

    public static void sleepAndCal(long ms, int coor) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gb.calibrateFpga(coor);
            }
        });
    }

    public static void sleepAndY(long ms, int coor) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gb.updatePaddleY(coor);
            }
        });
    }

    public static void main(String[] args) {
        (new BaseController(gb)).start();
        gb.main(args);
    }
}
