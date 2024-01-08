package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SwitchContentPane implements ActionListener {
    JFrame frame;
    Container contentPane;

    SwitchContentPane(JFrame frame, Container contentPane) {
        this.frame = frame;
        this.contentPane = contentPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setContentPane(contentPane);
        frame.revalidate();
    }

}
