package pong.control;

import javafx.application.Application;
import javafx.application.Platform;
import pong.Dimen;
import pong.gui.menu.MenuPane;

import java.awt.*;

/**
 * Created by Lindsay on 29-Oct-15.
 */
/* The great controller class:
    - Input from [communication]: the GPIO pin handler in C
    - Controls the [GUI]: in Java
    - Output to [communication]: the GPIO pin handler in C
 */
public class Controller extends Thread {
    private static MenuPane mp;

    public Controller(MenuPane menu_pane) {
        System.out.println("CONTROLLER");
        mp = menu_pane;
    }

    public void run() {
        System.out.println("RUN");
        test();
    }

    // Simulates pin handler updates
    public static void test() {
        // Gradually moves left paddle from bottom to top of field
        for (int i = 0; true; i++) {
            final int x = i;
            try {
                Thread.sleep((long) 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // Only show the paddle when it has been updated
                    System.out.println("Pane = " + mp.getPane().isVisible());
                    if (!mp.getPane().isVisible()) {
                        mp.getPane().setVisible(true);
                    }
//                    mp.updatePaddleY(100 + x * 10);
                    mp.updatePaddleY((int) (100 + Math.random() * 1000));
                }
            });
        }
    }

    public static void main(String[] args) {
        MenuPane.main(args);
    }
}
