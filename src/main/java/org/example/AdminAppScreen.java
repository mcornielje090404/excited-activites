package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminAppScreen {
    JFrame frame;
    Container contentPane;
    SpringLayout layout = new SpringLayout();
    SwitchContentPane mainMenuButtonActionListener;
    CustomerAppScreen customerAppScreen =  new CustomerAppScreen();

    public AdminAppScreen(JFrame frame, Container contentPane, SwitchContentPane mainMenuButtonActionListener) {
        this.frame = frame;
        this.contentPane = contentPane;
        this.mainMenuButtonActionListener = mainMenuButtonActionListener;
        contentPane.setLayout(layout);
    }

    public void createPanel() {
        contentPane.removeAll();

        CSVReader csvReader = new CSVReader();
        ArrayList<String[]> itineraryTableData = csvReader.readTable("Itinerary");
        ArrayList<Itinerary> itineraries = new ArrayList<>();

        for (String[] itineraryData : itineraryTableData) {
            itineraries.add(new Itinerary(itineraryData));
        }

        File backIconFile = new File("images/back-arrow.png");
        JButton backButton = new JButton("Back");

        if (backIconFile.exists()) {
            try {
                Image backImage = ImageIO.read(backIconFile).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                backButton.setIcon(new ImageIcon(backImage));
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

        backButton.addActionListener(mainMenuButtonActionListener);

        backButton.setPreferredSize(new Dimension(150, 75));
        contentPane.add(backButton);
        layout.putConstraint(SpringLayout.WEST, backButton, 0, SpringLayout.WEST, contentPane);

        DefaultTableModel itineraryTableModel = new DefaultTableModel(new String[]{"Lead attendee", "Total attendees", "Total activities", "Total cost"}, 0);

        for (Itinerary itinerary : itineraries) {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
            itineraryTableModel.addRow(new String[]{itinerary.getLeadAttendee().getFirstName().charAt(0) + " " + itinerary.getLeadAttendee().getLastName(), Integer.toString(itinerary.getNumOfAttendees()), Integer.toString(itinerary.getBookings().size()), currencyFormatter.format(customerAppScreen.getDiscountedTotal(itinerary) / 100)});
        }

        JTable itineraryTable = new JTable(itineraryTableModel);
        JScrollPane itineraryTableScrollPane = new JScrollPane(itineraryTable);
        itineraryTableScrollPane.setPreferredSize(new Dimension(600, 600));
        contentPane.add(itineraryTableScrollPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, itineraryTableScrollPane, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, itineraryTableScrollPane, 0, SpringLayout.VERTICAL_CENTER, contentPane);

        contentPane.revalidate();
        contentPane.repaint();
    }
}
