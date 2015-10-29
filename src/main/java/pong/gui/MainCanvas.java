package pong.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pong.Field;

public class MainCanvas extends Application {
    private static final double BALL_SCALE_X = 50;
    private static final double BALL_SCALE_Y = BALL_SCALE_X / Field.ASPECT_RATIO;
    private static final double PADDLE_SCALE_X = 50;
    private static final double PADDLE_SCALE_Y = 8;

    private StackPane pane;
    private Canvas canvas;
    private GraphicsContext gc;
    private double ballX;
    private double ballY;
    private double paddleLeft;
    private double paddleRight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        canvas = new Canvas();
        pane = new StackPane(canvas);
        pane.setAlignment(Pos.CENTER);
        pane.widthProperty().addListener(observable -> resize());
        pane.heightProperty().addListener(observable -> resize());
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        primaryStage.setScene(new Scene(pane, 1280, 720));
        primaryStage.show();
        System.out.println("Application Started");
    }

    private void redraw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.fillOval(
                ballX * canvas.getWidth(),
                ballY * canvas.getHeight(),
                canvas.getWidth() / BALL_SCALE_X,
                canvas.getHeight() / BALL_SCALE_Y
        );
        double paddleWidth = canvas.getWidth() / PADDLE_SCALE_X;
        double paddleHeight = canvas.getHeight() / PADDLE_SCALE_Y;
        gc.fillRect(
                0,
                paddleLeft * canvas.getHeight(),
                paddleWidth,
                paddleHeight
        );
        gc.fillRect(
                canvas.getWidth() - paddleWidth,
                paddleRight * canvas.getHeight(),
                paddleWidth,
                paddleHeight
        );
    }

    private void resize() {
        double width = pane.getWidth();
        double height = pane.getHeight();
        if (height >= width / Field.ASPECT_RATIO) {
            canvas.setWidth(width);
            canvas.setHeight(width / Field.ASPECT_RATIO);
        } else {
            canvas.setHeight(height);
            canvas.setWidth(height * Field.ASPECT_RATIO);
        }
        redraw();
    }

    public void ballX(int x) {
        ballX = x / Field.MAX_X;
        redraw();
    }

    public void ballY(int y) {
        ballY = y / Field.MAX_Y;
        redraw();
    }

    public void paddleLeft(int y) {
        paddleLeft = y / Field.MAX_Y;
        redraw();
    }

    public void paddleRight(int y) {
        paddleRight = y / Field.MAX_Y;
        redraw();
    }
}
