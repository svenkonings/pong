package pong.gui;

import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ButtonGroup extends Group {
    private ArrayList<MenuButton> buttons;

    public ButtonGroup() {
        super();
        init();
    }

    public ButtonGroup(Node... children) {
        super(children);
        init();
    }

    public ButtonGroup(Collection<Node> children) {
        super(children);
        init();
    }

    public void init() {
        buttons = new ArrayList<>();
    }

    public void addButtons(MenuButton... buttons) {
        Collections.addAll(this.buttons, buttons);
        getChildren().addAll(buttons);
    }

    public ArrayList<MenuButton> getButtons() {
        return buttons;
    }
}
