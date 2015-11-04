package pong.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ButtonGroup extends Group {
    private ArrayList<MenuButton> buttons;
    private ProgressBar bar;

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

    public ProgressBar addBar(GuiBase gb) {
        bar = new ProgressBar(0);
        bar.setStyle("-fx-accent: seagreen;");
        bar.setPrefWidth(gb.getFieldWidth() * (1 - GuiBase.FIELD_TO_BUTTON_WIDTH) * 0.5 * 0.75);
        bar.setPrefHeight(gb.getFieldY() * 0.75);
        bar.setLayoutX(gb.getFieldX() + gb.getFieldWidth() - gb.getFieldWidth() * (1 - GuiBase.FIELD_TO_BUTTON_WIDTH) * 0.5 * 0.5 - bar.getPrefWidth() / 2);// + (bar.getMaxWidth()) / 2);//fieldX / 2);
        bar.setLayoutY((gb.getFieldY() - bar.getPrefHeight()) / 2);
        return bar;
    }

    public void addButtons(MenuButton... buttons) {
        Collections.addAll(this.buttons, buttons);
        getChildren().addAll(buttons);
    }

    public ProgressBar getBar() {
        return bar;
    }

    public ArrayList<MenuButton> getButtons() {
        return buttons;
    }
}
