package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("App Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container customerScreenContentPane = new Container();
        Container mainMenuContentPane = new Container();

        MainMenu mainMenu = getMainMenu(frame, customerScreenContentPane, mainMenuContentPane);
        mainMenu.createPanel();

        frame.setContentPane(mainMenuContentPane);

        frame.pack();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static MainMenu getMainMenu(JFrame frame, Container customerScreenContentPane, Container mainMenuContentPane) {
        SwitchContentPane customerAppButtonActionListener = new SwitchContentPane(frame, customerScreenContentPane);
        SwitchContentPane mainMenuButtonActionListener = new SwitchContentPane(frame, mainMenuContentPane);

        CustomerAppScreen customerAppScreen = new CustomerAppScreen(frame, customerScreenContentPane, mainMenuButtonActionListener);
        customerAppScreen.createPanel();

        return new MainMenu(mainMenuContentPane, customerAppButtonActionListener, mainMenuButtonActionListener);
    }
}