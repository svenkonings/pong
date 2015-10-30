package pong.gui.menu;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Dimen;
import pong.control.Controller;
import pong.gui.Paddle;

import javax.sound.sampled.*;
import java.io.*;

public class MenuPane extends Application {
    private static double SCREEN_WIDTH = 1080, SCREEN_HEIGHT = 720; // Default values, would be overwritten immediately
    private static double PADDLE_HEIGHT; // Dependant on screen width and height
    private static double PADDLE_WIDTH = PADDLE_HEIGHT / 4;
    private static double PADDLE_Y = SCREEN_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private static double PADDLE_X = 20; // Should snap onto playing field
    private static Pane pane;
    private static Paddle paddle;
    private static Button selected;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        calibrateGui();

        // Calibration TODO Move this to Controller.test()
        Dimen.FPGA_MIN_Y = 100;
        Dimen.FPGA_MAX_Y = 1100;
        Dimen.FPGA_PADDLE_HEIGHT = 250;

        // Pane set-up
        pane = new Pane();
        pane.setMaxHeight(SCREEN_HEIGHT);
        pane.setMinHeight(SCREEN_HEIGHT);
//        pane.setVisible(false);

        // TODO Move this to post-calibration
        PADDLE_HEIGHT = Dimen.FPGA_PADDLE_HEIGHT * (Dimen.FPGA_MAX_Y - Dimen.FPGA_MIN_Y) / SCREEN_HEIGHT;
        PADDLE_WIDTH = PADDLE_HEIGHT / 4;
        setUpPaddle();

        // Stage set-up
        stage.centerOnScreen();
        stage.setHeight(SCREEN_HEIGHT);
        stage.setScene(new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT));
        stage.show();

        // Test
        test();
    }

    public void setUpPaddle() {
        paddle = new Paddle(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFill(Color.BLUE);
        paddle.setX(PADDLE_X);
        pane.getChildren().add(paddle);
    }

    public static void calibrateGui() {
        for (Screen scr : Screen.getScreens()) {
            System.out.println(scr);
        }
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        SCREEN_WIDTH = bounds.getWidth();
        SCREEN_HEIGHT = bounds.getHeight();
        PADDLE_HEIGHT = SCREEN_HEIGHT * Dimen.FPGA_PADDLE_HEIGHT / (Dimen.FPGA_MAX_Y - Dimen.FPGA_MIN_Y);
    }

    // Updates the y position of the paddle according to the value given by the FPGA
    public static void updatePaddleY(int fpgaY) {
        // Convert from FPGA format to GUI format
        System.out.println("fpgaY = " + fpgaY + ", pane height = " + pane.getHeight());
        double y = Dimen.fpga2guiY(fpgaY, pane.getHeight());
        System.out.println("guiy = " + y + ", paddle height = " + PADDLE_HEIGHT);
        paddle.setY(y);
        System.out.println("PADDLE W = " + paddle.getWidth() + ", H = " + paddle.getHeight());
        for (Node child : pane.getChildrenUnmodifiable()) {
            // Loop through all the buttons of this pane
            if (child instanceof Button) {
                Button button = (Button) child;
                // Test if the middle of the paddle is somewhere between the top and the bottom of the button
                if (button.containsY(y + paddle.getHeight() / 2)) {
                    // Button selected
                    button.setFill(Color.LIGHTGRAY);
                    selected = button;
                } else {
                    // Button deselected
                    button.setFill(Color.DARKGRAY);
                }
            }
        }
    }

    public void click() {
        // If there is a selected button
        if (selected != null) {
            // Execute the code of the selected button
            selected.click();
        }
    }

    public void startMusic() {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("music.wav"));
            clip.open(ais);
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createButton() {
        int N = 3, margin = 40;
        for (int i = 0; i < N; i++) {
            double h = (pane.getHeight() - (N + 1) * margin) / N;
            Button button = new Button(pane.getWidth() * 0.75, h) {
                @Override
                public void click() {
                    System.out.println(paddle.getY());
                }
            };
            button.setX((pane.getWidth() - button.getWidth()) / 2);
            button.setY( i * h + (i + 1) * margin);
            pane.getChildren().add(button);
        }
    }

    public static Pane getPane() {
        return pane;
    }

    private void test() {
//        startMusic();
        System.out.println("TEST");
        createButton();
        Text t = new Text("Welkom bij PaddleBall. Als je wil beginnen met calibreren, klik op knop 1 van de FPGA.");
        pane.getChildren().add(t);
        t.setY(pane.getHeight() - t.getBoundsInParent().getHeight());
        t.setX((pane.getWidth() - t.getBoundsInParent().getWidth())/ 2);
        (new Controller(this)).start();
        System.out.println("START DONE");
        click();
    }
}
