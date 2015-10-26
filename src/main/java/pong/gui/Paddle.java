package pong.gui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {
    public Paddle() {
    }

    public Paddle(double width, double height) {
        super(width, height);
    }

    public Paddle(double width, double height, Paint fill) {
        super(width, height, fill);
    }

    public Paddle(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
}
