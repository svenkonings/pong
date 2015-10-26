package pong.gui.menu;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public abstract class Button extends Rectangle {
    public Button() {
    }

    public Button(double width, double height) {
        super(width, height);
    }

    public Button(double width, double height, Paint fill) {
        super(width, height, fill);
    }

    public Button(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public abstract void click();

    public boolean containsY(double y) {
        return getY() <= y && y <= getY() + getHeight();
    }
}
