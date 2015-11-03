package pong.gui;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
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

    public Text addText(String s) {
        string = s;
        text = new Text(s);
        text.setFont(new Font("Verdana", 100));
        text.setTextOrigin(VPos.TOP);
        text.setFill(GuiBase.getPRESSED());
        locateText();
        return text;
    }

    public void locateText() {
        Bounds bounds = text.getBoundsInParent();
        double x = getX() + (getWidth() - bounds.getWidth()) / 2;
        double y = getY() + (getHeight() - bounds.getHeight()) / 2;
        text.setX(x);
        text.setY(y);
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
