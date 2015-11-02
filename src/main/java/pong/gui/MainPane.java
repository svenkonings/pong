package pong.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pong.Fpga;
import pong.gpio.Gpio;

public class MainPane extends Application implements Gpio.Listener {
    private Gpio gpio;
    private int count, previous;
    private StackPane root;
    private Pane pane;
    private Circle ball;
    private Rectangle paddleLeft;
    private Rectangle paddleRight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gpio = new Gpio(this);
        gpio.start();
        pane = new Pane();
        pane.setStyle("-fx-background-color: gray");
        root = new StackPane(pane);
        root.setStyle("-fx-background-color: white");
        root.setAlignment(Pos.CENTER);
        root.widthProperty().addListener(observable -> resize());
        root.heightProperty().addListener(observable -> resize());
        ball = new Circle(12.5, Color.RED);
        paddleLeft = new Rectangle(12.5, 50);
        paddleLeft.setFill(Color.BLUE);
        paddleRight = new Rectangle(12.5, 50);
        paddleRight.setFill(Color.BLUE);
        pane.getChildren().addAll(ball, paddleLeft, paddleRight);
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
        ball.setCenterX(pane.getWidth() / 2);
        ball.setCenterY(pane.getHeight() / 2);
        paddleRight.setX(pane.getWidth() - paddleRight.getWidth());
        gpio.send(Gpio.CALIBRATION);
        System.out.println("Send calibration");
        System.out.println("Application Started");
    }

    private void resize() {
        double width = root.getWidth();
        double height = root.getHeight();
        if (height >= width / 2) {
            pane.setMaxSize(width, width / 2);
        } else {
            pane.setMaxSize(height * 2, height);
        }
    }

    @Override
    public void paddleLeft(int y) {
        if (count >= 0 && count++ >= 3000) {
            count = -1;
            gpio.send(Gpio.START_GAME);
            System.out.println("Send start game");
        }
        paddleLeft.setY(pane.getHeight() * (y / 1250.0));
    }

    @Override
    public void goalLeft() {

    }

    @Override
    public void paddleRight(int y) {
        paddleRight.setY(pane.getHeight() * (y / 1250.0));
    }

    @Override
    public void goalRight() {

    }

    @Override
    public void ballX(int x) {
        ball.setCenterX(pane.getWidth() * (x / 2500));
    }

    @Override
    public void collision() {

    }

    @Override
    public void ballY(int y) {
        ball.setCenterY(pane.getHeight() * (y / 1250.0));
    }

    @Override
    public void calibration(int value) {
        if (previous != value) {
            previous = value;
            System.out.println("calibration " + count++ + ": " + value);
            if (count > 2) {
                count = 0;
                gpio.send(Gpio.MENU);
                System.out.println("Send Menu");
            }
        }
    }
}