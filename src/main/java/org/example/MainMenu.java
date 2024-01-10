package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainMenu {
    Container contentPane;
    SpringLayout layout = new SpringLayout();
    SwitchContentPane customerAppButtonActionListener;
    SwitchContentPane adminAppButtonActionEvent;


    MainMenu(Container contentPane, SwitchContentPane customerAppButtonActionListener, SwitchContentPane adminAppButtonActionListener) {
        this.contentPane = contentPane;
        this.customerAppButtonActionListener = customerAppButtonActionListener;
        this.adminAppButtonActionEvent = adminAppButtonActionListener;
        contentPane.setLayout(layout);
    }

    public void createPanel() {
        JButton customerAppButton = new JButton("Customer App");
        customerAppButton.setPreferredSize(new Dimension(150, 75));

        JButton adminAppButton = new JButton("Admin App");
        adminAppButton.setPreferredSize(new Dimension(150, 75));

        contentPane.add(customerAppButton);
        contentPane.add(adminAppButton);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, customerAppButton, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, customerAppButton, 50, SpringLayout.VERTICAL_CENTER, contentPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, adminAppButton, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, adminAppButton, -50, SpringLayout.VERTICAL_CENTER, contentPane);

        customerAppButton.addActionListener(customerAppButtonActionListener);
    }
}
