package pong;

import pong.gpio.Gpio;

public class Tui implements Gpio.Listener {
    private Gpio gpio;
    private int count, previous;

    public static void main(String[] args) {
        Tui tui = new Tui();
        tui.gpio = new Gpio(tui);
        tui.gpio.start();
        try {
            Thread.sleep(10); // Wait for the gpio thread to start
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tui.gpio.send(Gpio.CALIBRATION);
        printYellow("Send calibration");
    }

    public static void printYellow(String print) {
        System.out.println("\u001B[33m" + print + "\u001B[0m");
    }

    public static void printBlue(String print) {
        System.out.println("\u001B[34m" + print + "\u001B[0m");
    }

    @Override
    public void paddleLeft(int y) {
        if (count >= 0) {
            if (previous != y) {
                previous = y;
                printYellow("paddleLeft" + count++ + ": " + y);
                if (count > 200) {
                    count = -1;
                    gpio.send(Gpio.START_GAME);
                    printYellow("Send start game");
                }
            }
        } else {
            printYellow("paddleLeft" + ": " + y);
        }
    }

    @Override
    public void goalLeft() {
        printBlue("goalLeft");
    }

    @Override
    public void paddleRight(int y) {
        printYellow("paddleRight" + ": " + y);
    }

    @Override
    public void goalRight() {
        printBlue("goalRight");
    }

    @Override
    public void ballX(int x) {
        printYellow("ballX" + ": " + x);
    }

    @Override
    public void collision() {
        printBlue("collision");
    }

    @Override
    public void ballY(int y) {
        printYellow("ballY" + ": " + y);
    }

    @Override
    public void calibration(int value) {
        if (previous != value) {
            previous = value;
            printYellow("calibration " + count++ + ": " + value);
            if (count > 2) {
                count = 0;
                gpio.send(Gpio.MENU);
                printYellow("Send Menu");
            }
        }
    }
}
