package pong.control;

import javafx.application.Platform;
import pong.gui.menu.MenuPane;

/**
 * Created by Lindsay on 29-Oct-15.
 */
public class Controller extends Thread {
    private MenuPane mp;

    public Controller(MenuPane menu_pane) {
        System.out.println("Controller started!");
        mp = menu_pane;
    }

    public void run() {
        for (int i = 0; i < 200; i++) {
            final int x = i;
            try {
                Thread.sleep((long) 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Pane visible = " + mp.getPane().isVisible());
                    if (!mp.getPane().isVisible()) {
                        mp.getPane().setVisible(true);
                    }
                    mp.updatePaddleY(x * 10);
                }
            });
        }
    }
}
