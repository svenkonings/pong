package pong.gui.menu;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pong.Field;
import pong.gui.Paddle;

public class MenuPane extends Application {
    private Pane pane;
    private Paddle paddle;
    private Button selected;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        paddle = new Paddle(20, 50);
        paddle.setFill(Color.BLUE);
        pane = new Pane(paddle);
        primaryStage.setScene(new Scene(pane, 1280, 720));
        primaryStage.show();
        test(); // Temporary
    }

    public void paddleY(int fieldY) {
        double y = (fieldY / Field.MAX_Y) * pane.getHeight();
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

    private void test() {
        Button button = new Button(100, 100, 20, 50) {
            @Override
            public void click() {
                System.out.println(paddle.getY());
            }
        };
        button.setFill(Color.BLUE);
        pane.getChildren().add(button);
        paddleY(300);
        click();
    }
}
