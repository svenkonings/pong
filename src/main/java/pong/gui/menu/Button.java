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

    /**
     * Override this method to add code that is executed when the button is clicked.
     */
    public abstract void click();

    /**
     * Tests if the y coordinate is inside the vertical boundary of the button.
     *
     * @param y - the y coordinate
     * @return true if the y coordinate is inside the vertical boundary.
     */
    public boolean containsY(double y) {
        return getY() <= y && y <= getY() + getHeight();
    }
}
