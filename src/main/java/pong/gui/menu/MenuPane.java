package pong.gui.menu;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pong.Dimen;
import pong.control.Controller;
import pong.gui.Paddle;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MenuPane extends Application {
    private final static double PADDLE_WIDTH = 20; // Might be changed to width scaled with the paddle height
    private final static double PADDLE_X = 50;
    private static double SCREEN_WIDTH = 1080, SCREEN_HEIGHT = 720; // Default values, would be overwritten immediately
    private static double PADDLE_HEIGHT; // Dependant on screen width and height
    private Pane pane;
    private Paddle paddle;
    private Button selected;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        for (Screen scr : Screen.getScreens()) {
            System.out.println(scr);
        }
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        SCREEN_WIDTH = bounds.getWidth();
        SCREEN_HEIGHT = bounds.getHeight();
        PADDLE_HEIGHT = SCREEN_HEIGHT * Dimen.REAL_PADDLE_HEIGHT / Dimen.REAL_MAX_Y;
        paddle = new Paddle(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFill(Color.BLUE);
        paddle.setX(PADDLE_X);
        pane = new Pane(paddle);
        pane.setMaxHeight(SCREEN_HEIGHT);
        pane.setMinHeight(SCREEN_HEIGHT);
        pane.setVisible(false);
//        primaryStage.setFullScreen(true);
        primaryStage.centerOnScreen();
//        primaryStage.setResizable(false);
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setScene(new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT));
        System.out.println("Stage height = " + primaryStage.getHeight() + ", scene height = " + primaryStage.getScene().getHeight() + ", pane height = " + pane.getHeight());
        primaryStage.show();
        System.out.println("Stage height = " + primaryStage.getHeight() + ", scene height = " + primaryStage.getScene().getHeight() + ", pane height = " + pane.getHeight());
        test(); // Temporary
    }

    // Updates the y position of the paddle according to the value given by the FPGA
    public void updatePaddleY(int fpgaY) {
        // Convert from FPGA format to GUI format
        System.out.println("fpgaY = " + fpgaY + ", pane height = " + pane.getHeight());
        double y = Dimen.fpga2guiY(fpgaY, pane.getHeight());
        System.out.println("guiy = " + y + ", paddle height = " + PADDLE_HEIGHT);
        paddle.setY(y);
        Button selectedNew = null;
        for (Node child : pane.getChildrenUnmodifiable()) {
            // Loop through all the buttons of this pane
            if (child instanceof Button) {
                Button button = (Button) child;
                // Test if the middle of the paddle is somewhere between the top and the bottom of the button
                if (button.containsY(y + paddle.getHeight() / 2)) {
                    // Button selected
                    button.setFill(Color.RED);
                    selectedNew = button;
                } else {
                    // Button deselected
                    button.setFill(Color.BLUE);
                }
            }
        }
        // Update selected button
        selected = selectedNew;
    }

    public void click() {
        // If there is a selected button
        if (selected != null) {
            // Execute the code of the selected button
            selected.click();
        }
    }

    public void startMusic() {
//        AudioClip ac = new AudioClip(new File("music.mp3").toURI().toString());
//        ac.play();

        try {
            Clip clip = AudioSystem.getClip();
//            URL url = this.getClass().getResource("music.mp3");
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

//        URI url = null;
//        try {
//            url = new URI("music.mp3");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        Clip clip = null;
//        try {
//            clip = AudioSystem.getClip();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        }
//        // getAudioInputStream() also accepts a File or InputStream
//        AudioInputStream ais = null;
//        try {
//            ais = AudioSystem.getAudioInputStream(MenuPane.class.getResourceAsStream(url));
//            try {
//                clip.open(ais);
//            } catch (LineUnavailableException e) {
//                e.printStackTrace();
//            }
//            clip.loop(Clip.LOOP_CONTINUOUSLY);
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    // A GUI element to prevent the Clip's daemon Thread
//                    // from terminating at the end of the main()
//                    JOptionPane.showMessageDialog(null, "Close to exit!");
//                }
//            });
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Pane getPane() {
        return pane;
    }

    private void test() {
        // TODO Krijg min en max FPGA format binnen, en dan updates naar die format
        Button button = new Button(100, 100, 20, 50) {
            @Override
            public void click() {
                System.out.println(paddle.getY());
            }
        };
        startMusic();
        button.setFill(Color.BLUE);
        pane.getChildren().add(button);
        (new Controller(this)).start();
        click();
    }
}
