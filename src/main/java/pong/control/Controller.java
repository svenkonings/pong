package pong.control;

import javafx.application.Platform;
import pong.gui.GUI;

/**
 * Created by Lindsay on 29-Oct-15.
 */
/* The great controller class:
    - Input from [communication]: the GPIO pin handler in C
    - Controls the [GUI]: in Java
    - Output to [communication]: the GPIO pin handler in C
 */
public class Controller extends Thread {
    private static GUI mp;

    public Controller(GUI menu_pane) {
        mp = menu_pane;
    }

    public void run() {
        test();
    }

    // Simulates pin handler updates
    public static void test() {
        // Gradually moves left paddle from bottom to top of field
        for (int i = 0; true; i++) {
            final int x = i;
            try {
                Thread.sleep((long) 1000);
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
        GUI.main(args);
    }
}
