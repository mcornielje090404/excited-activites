package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CustomerAppScreen {
    JFrame frame;
    Container contentPane;
    SpringLayout layout = new SpringLayout();
    SwitchContentPane mainMenuButtonActionListener;
    ArrayList<Activity> activities = new ArrayList<>();
    Activity selectedActivity;

    CustomerAppScreen(JFrame frame, Container contentPane, SwitchContentPane mainMenuButtonActionListener) {
        this.frame = frame;
        this.contentPane = contentPane;
        this.mainMenuButtonActionListener = mainMenuButtonActionListener;
        contentPane.setLayout(layout);

        CSVReader csvReader = new CSVReader();
        ArrayList<String[]> activityTableData = csvReader.readTable("Activity");

        for (String[] activityData : activityTableData) {
            activities.add(new Activity(activityData));
        }
    }

    public void createPanel() {
        this.getActivityListDisplay();
    }

    public void getActivityListDisplay() {
        contentPane.removeAll();
        this.getHeaderButtons(mainMenuButtonActionListener);

        String[] activityHeaders = { "Title", "Description", "Location", "Date", "Duration", "Price from" };
        ArrayList<String[]> activityRows = new ArrayList<>();

        for (Activity activity : activities) {
            LocalDateTime dateTime = LocalDateTime.parse(activity.getActivityDate());
            String[] activityRow = { activity.getTitle(), activity.getDescription(), activity.getLocation(), dateTime.toString(), String.valueOf(activity.getDuration()), String.valueOf(activity.getBaseCost()) };
            activityRows.add(activityRow);
        }

        JTable activityTable = new JTable(activityRows.toArray(new String[0][0]), activityHeaders) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public void valueChanged(ListSelectionEvent evt) {
                super.valueChanged(evt);
                selectedActivity = activities.get(this.getSelectedRow());
                getSelectedActivityDisplay();
                frame.revalidate();
            }
        };
        activityTable.setBounds(30, 40, 600, 100);
        activityTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane tablePane = new JScrollPane(activityTable);
        tablePane.setPreferredSize(new Dimension(600, 400));

        contentPane.add(tablePane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, tablePane, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, tablePane, 0, SpringLayout.VERTICAL_CENTER, contentPane);

        contentPane.repaint();
        contentPane.revalidate();
    }

    private void getSelectedActivityDisplay() {
        contentPane.removeAll();
        this.getHeaderButtons(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getActivityListDisplay();
            }
        });

        JLabel label = new JLabel(selectedActivity.getTitle());
        label.setFont(new Font("Arial", Font.PLAIN, 30));
        contentPane.add(label);
        layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, label, 95, SpringLayout.NORTH, contentPane);


        contentPane.repaint();
    }

    public void getHeaderButtons(ActionListener backButtonActionListener) {
        File basketImageFile = new File("images/cart.png");
        JButton basketButton = new JButton("£250.54");

        if (basketImageFile.exists()) {
            try {
                Image basketImage = ImageIO.read(basketImageFile).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                basketButton.setIcon(new ImageIcon(basketImage));
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

        File backIconFile = new File("images/back-arrow.png");
        JButton backButton = new JButton("Back");

        if (basketImageFile.exists()) {
            try {
                Image backImage = ImageIO.read(backIconFile).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                backButton.setIcon(new ImageIcon(backImage));
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

        backButton.addActionListener(backButtonActionListener);

        basketButton.setPreferredSize(new Dimension(150, 75));
        contentPane.add(basketButton);
        layout.putConstraint(SpringLayout.EAST, basketButton, 0, SpringLayout.EAST, contentPane);

        backButton.setPreferredSize(new Dimension(150, 75));
        contentPane.add(backButton);
        layout.putConstraint(SpringLayout.WEST, backButton, 0, SpringLayout.WEST, contentPane);
    }
}
