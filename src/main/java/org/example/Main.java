package org.example;

<<<<<<< Updated upstream
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
=======
import javax.swing.*;
import java.awt.*;

>>>>>>> Stashed changes
public class Main {
    public static void main(String[] args) {

<<<<<<< Updated upstream
=======
        Container customerScreenContentPane = new Container();
        Container mainMenuContentPane = new Container();
        Container adminScreenContentPane = new Container();

        MainMenu mainMenu = getMainMenu(frame, customerScreenContentPane, mainMenuContentPane, adminScreenContentPane);
        mainMenu.createPanel();

        frame.setContentPane(mainMenuContentPane);

        frame.pack();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static MainMenu getMainMenu(JFrame frame, Container customerScreenContentPane, Container mainMenuContentPane, Container adminScreenContentPane) {
        SwitchContentPane mainMenuButtonActionListener = new SwitchContentPane(frame, mainMenuContentPane);

        CustomerAppScreen customerAppScreen = new CustomerAppScreen(frame, customerScreenContentPane, mainMenuButtonActionListener);
        customerAppScreen.createPanel();

        AdminAppScreen adminAppScreen = new AdminAppScreen(frame, adminScreenContentPane, mainMenuButtonActionListener);
        adminAppScreen.createPanel();

        SwitchContentPane customerAppButtonActionListener = new SwitchContentPane(frame, customerScreenContentPane);
        SwitchContentPane adminAppButtonActionListener = new SwitchContentPane(frame, adminScreenContentPane) {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                super.actionPerformed(e);
                adminAppScreen.createPanel();
            }

        };

        return new MainMenu(mainMenuContentPane, customerAppButtonActionListener, adminAppButtonActionListener);
>>>>>>> Stashed changes
    }
}