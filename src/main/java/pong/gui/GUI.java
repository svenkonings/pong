package pong.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Fpga;
import pong.control.Controller;

import javax.sound.sampled.*;
import java.io.*;

public class GUI extends Application {
    private static double SCREEN_WIDTH = 1080, SCREEN_HEIGHT = 720; // Default values, would be overwritten immediately
    private static double PADDLE_HEIGHT; // Dependant on screen width and height
    private static double PADDLE_WIDTH = PADDLE_HEIGHT / 4;
    private static double PADDLE_Y = SCREEN_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private static double PADDLE_X = 20; // Should snap onto playing field
    private static double FIELD_WIDTH, FIELD_HEIGHT, FIELD_X_OFFSET, FIELD_Y_OFFSET;
    private static Pane pane;
    private static Paddle paddle;
    private static MenuButton selected;
    private static Text t;
    private static Rectangle field;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        calibrateGui();

        // Calibration TODO Move this to Controller.test()
        Fpga.MIN_Y = 100;
        Fpga.MAX_Y = 1100;
        Fpga.PADDLE_Y = 250;

        // Pane set-up
        pane = new Pane();
        pane.setMaxHeight(SCREEN_HEIGHT);
        pane.setMinHeight(SCREEN_HEIGHT);
//        pane.setVisible(false);


        // TODO Move this to post-calibration
        PADDLE_HEIGHT = Fpga.PADDLE_Y * (Fpga.MAX_Y - Fpga.MIN_Y) / SCREEN_HEIGHT;
        PADDLE_WIDTH = PADDLE_HEIGHT / 4;
        setUpPaddle();

        // Stage set-up
        stage.centerOnScreen();
        stage.setHeight(SCREEN_HEIGHT);
        stage.setScene(new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT));
        stage.show();

        // Field set-up
        FIELD_WIDTH = pane.getWidth() - 2 * PADDLE_HEIGHT;
        FIELD_HEIGHT = FIELD_WIDTH / 2;
        double FIELD_X_OFFSET = PADDLE_HEIGHT, FIELD_Y_OFFSET = (pane.getHeight() - FIELD_HEIGHT) / 2;
        field = new Rectangle(FIELD_X_OFFSET, FIELD_Y_OFFSET, FIELD_WIDTH, FIELD_HEIGHT);
        field.setFill(Color.ANTIQUEWHITE);
        pane.getChildren().add(field);

        // Test
        test();
    }

    public void setUpPaddle() {
        paddle = new Paddle(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFill(Color.BLUE);
        paddle.setX(FIELD_X_OFFSET + PADDLE_X);
        paddle.setY(FIELD_Y_OFFSET - PADDLE_HEIGHT);
        pane.getChildren().add(paddle);
    }

    public static void calibrateGui() {
        for (Screen scr : Screen.getScreens()) {
            System.out.println(scr);
        }
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        SCREEN_WIDTH = bounds.getWidth();
        SCREEN_HEIGHT = bounds.getHeight();
        PADDLE_HEIGHT = SCREEN_HEIGHT * Fpga.PADDLE_Y / (Fpga.MAX_Y - Fpga.MIN_Y);
    }

    // Updates the y position of the paddle according to the value given by the FPGA
    public static void updatePaddleY(int fpgaY) {
        t.setText("Binnengekomen waarde: " + fpgaY);
        t.setY(FIELD_Y_OFFSET + FIELD_HEIGHT - t.getBoundsInParent().getHeight());
        t.setX(FIELD_X_OFFSET + (FIELD_WIDTH - t.getBoundsInParent().getWidth())/ 2);
        // Convert from FPGA format to GUI format
        double y = Fpga.convertPaddleY(fpgaY, pane.getHeight());
        y += FIELD_Y_OFFSET;
        paddle.setY(y);
        for (Node child : pane.getChildrenUnmodifiable()) {
            // Loop through all the buttons of this pane
            if (child instanceof MenuButton) {
                MenuButton menuButton = (MenuButton) child;
                // Test if the middle of the paddle is somewhere between the top and the bottom of the menuButton
                if (menuButton.containsY(y + paddle.getHeight() / 2)) {
                    // MenuButton selected
                    menuButton.setFill(Color.LIGHTGRAY);
                    selected = menuButton;
                } else {
                    // MenuButton deselected
                    menuButton.setFill(Color.DARKGRAY);
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
            double h = (FIELD_Y_OFFSET - (N + 1) * margin) / N;
            MenuButton menuButton = new MenuButton(FIELD_WIDTH * 0.75, h) {
                @Override
                public void click() {
                    System.out.println(paddle.getY());
                }
            };
            menuButton.setX(FIELD_X_OFFSET + (FIELD_WIDTH - menuButton.getWidth()) / 2);
            menuButton.setY(FIELD_Y_OFFSET + i * h + (i + 1) * margin);
            pane.getChildren().add(menuButton);
        }
    }

    public static Pane getPane() {
        return pane;
    }

    private void test() {
//        startMusic();
        createButton();
        t = new Text("Welkom bij PaddleBall. Als je wil beginnen met calibreren, klik op knop 1 van de FPGA.");
        pane.getChildren().add(t);
        t.setY(FIELD_Y_OFFSET + FIELD_HEIGHT - t.getBoundsInParent().getHeight());
        t.setX(FIELD_X_OFFSET + (FIELD_WIDTH - t.getBoundsInParent().getWidth())/ 2);
        (new Controller(this)).start();
        click();
    }
}
