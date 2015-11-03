package pong.gui;

import javafx.geometry.Bounds;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuButton extends Rectangle {
    private Runnable click;
    private String string;
    private Text text;

    public MenuButton() {
        super();
    }

    public MenuButton(double width, double height) {
        super(width, height);
    }

    public MenuButton(double width, double height, Paint fill) {
        super(width, height, fill);
    }

    public MenuButton(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public Runnable getClick() {
        return click;
    }

    public void setClick(Runnable click) {
        this.click = click;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void addText(String s) {
        string = s;
        text = new Text(s);
        text.setFont(new Font("Verdana", 100));
        locateText();
    }

    public void locateText() {
        Bounds bounds = text.getBoundsInParent();
        System.out.println("bounds = " + bounds);
        text.setX(getX() + (getWidth() - bounds.getWidth()) / 2);
        text.setY(getY() + (getHeight() - bounds.getHeight()) / 2);
    }

    /**
     * Override this method to add code that is executed when the button is clicked.
     */
    public void click() {
        if (click != null) {
            click.run();
        }
    }

    /**
     * Tests if the y coordinate is within the vertical boundaries of the button.
     *
     * @param y - the y coordinate of the top of the paddle
     * @return true if the y coordinate is within the vertical boundaries
     */
    public boolean containsY(double y) {
        return getY() <= y && y <= getY() + getHeight();
    }
}
