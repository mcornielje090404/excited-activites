package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Function;

public class CustomerAppScreen {
    JFrame frame;
    Container contentPane;
    SpringLayout layout = new SpringLayout();
    SwitchContentPane mainMenuButtonActionListener;
    ArrayList<Activity> activities = new ArrayList<>();
    Itinerary itinerary = new Itinerary();
    Activity selectedActivity;
    JButton basketButton = new JButton("£0.00");


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
        this.renderActivityListDiplsay();
    }

    public void renderActivityListDiplsay() {
        contentPane.removeAll();
        this.getHeaderButtons(mainMenuButtonActionListener);

        String[] activityHeaders = {"Title", "Location", "Date", "Duration", "Price from"};
        JTable activityTable = getActivityTable(activityHeaders);
        activityTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        activityTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane tablePane = new JScrollPane(activityTable);
        tablePane.setPreferredSize(new Dimension(600, 400));

        contentPane.add(tablePane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, tablePane, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, tablePane, 0, SpringLayout.VERTICAL_CENTER, contentPane);

        contentPane.repaint();
        contentPane.revalidate();
    }

    private JTable getActivityTable(String[] activityHeaders) {
        ArrayList<String[]> activityRows = new ArrayList<>();
        ArrayList<Activity> itineraryActivities = itinerary.getAllActivities();
        ArrayList<Activity> filteredActivities = new ArrayList<>();

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

        for (Activity activity : activities) {
            if (itineraryActivities.stream().noneMatch(itineraryActivity -> itineraryActivity.getId().equals(activity.getId()))) {
                filteredActivities.add(activity);

                OffsetDateTime date = OffsetDateTime.parse(activity.getActivityDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                String[] activityRow = {activity.getTitle(), activity.getLocation(), dateFormatter.format(date), String.valueOf(activity.getDuration()), currencyFormatter.format(activity.getBaseCost() / 10)};
                activityRows.add(activityRow);
            }
        }

        return createActivityTable(activityHeaders, activityRows, filteredActivities);
    }

    private JTable createActivityTable(String[] activityHeaders, ArrayList<String[]> activityRows, ArrayList<Activity> filteredActivities) {
        JTable activityTable = new JTable(activityRows.toArray(new String[0][0]), activityHeaders) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public void valueChanged(ListSelectionEvent evt) {
                super.valueChanged(evt);
                selectedActivity = filteredActivities.get(this.getSelectedRow());
                renderSelectedActivityDisplay();
                frame.revalidate();
            }
        };
        activityTable.setBounds(30, 40, 600, 100);
        return activityTable;
    }

    private void renderSelectedActivityDisplay() {
        contentPane.removeAll();

        this.getHeaderButtons(e -> renderActivityListDiplsay());

        JLabel label = new JLabel(selectedActivity.getTitle());
        label.setFont(new Font("Arial", Font.PLAIN, 30));

        contentPane.add(label);
        layout.putConstraint(SpringLayout.WEST, label, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, label, 120, SpringLayout.NORTH, contentPane);

        OffsetDateTime date = OffsetDateTime.parse(selectedActivity.getActivityDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        JLabel locationLabel = new JLabel("Location: " + selectedActivity.getLocation());
        JLabel dateLabel = new JLabel("Date: " + formatter.format(date));
        JLabel durationLabel = new JLabel("Duration: " + selectedActivity.getDuration() + " minutes");

        locationLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        durationLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        contentPane.add(locationLabel);
        layout.putConstraint(SpringLayout.WEST, locationLabel, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, locationLabel, 155, SpringLayout.WEST, contentPane);

        contentPane.add(dateLabel);
        layout.putConstraint(SpringLayout.WEST, dateLabel, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, dateLabel, 175, SpringLayout.WEST, contentPane);

        contentPane.add(durationLabel);
        layout.putConstraint(SpringLayout.WEST, durationLabel, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, durationLabel, 195, SpringLayout.WEST, contentPane);

        JTextArea textArea = new JTextArea(selectedActivity.getDescription());
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane textFieldPane = new JScrollPane(textArea);
        textFieldPane.setPreferredSize(new Dimension(700, 250));

        contentPane.add(textFieldPane);
        layout.putConstraint(SpringLayout.WEST, textFieldPane, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, textFieldPane, 250, SpringLayout.WEST, contentPane);

        ArrayList<ActivityService> services = selectedActivity.getServices();
        ArrayList<ActivityService> selectedServices = new ArrayList<>();

        float totalCost = selectedActivity.getBaseCost() + sumServicePrice(selectedServices);

        DefaultTableModel servicesModel = new DefaultTableModel(new String[]{"Add on", "Price", ""}, 0);
        DefaultTableModel selectedServicesModel = new DefaultTableModel(new String[]{"Add on", "Price", ""}, 0);

        JLabel totalCostLabel = new JLabel("Total Cost: " + NumberFormat.getCurrencyInstance(Locale.UK).format(totalCost / 10));
        totalCostLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        contentPane.add(totalCostLabel);
        layout.putConstraint(SpringLayout.EAST, totalCostLabel, -50, SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, totalCostLabel, 275, SpringLayout.VERTICAL_CENTER, contentPane);

        Function<Void, Void> valueAdjuster = (Void) -> {
            float total = (float) (selectedActivity.getBaseCost() + sumServicePrice(selectedServices)) / 10;
            totalCostLabel.setText("Total Cost: " + NumberFormat.getCurrencyInstance(Locale.UK).format(total));
            return null;
        };

        JButton addToBasketButton = getAddToBasketButton(selectedServices);

        contentPane.add(addToBasketButton);
        layout.putConstraint(SpringLayout.WEST, addToBasketButton, 50, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, addToBasketButton, 275, SpringLayout.VERTICAL_CENTER, contentPane);

        JTable serviceTable = getServicesTable(services, selectedServices, servicesModel, selectedServicesModel, "Remove", valueAdjuster);
        JTable selectedServicesTable = getServicesTable(selectedServices, services, selectedServicesModel, servicesModel, "Add", valueAdjuster);

        this.renderServiceLabel(SpringLayout.WEST, 50, "Available Services");
        this.renderServiceLabel(SpringLayout.EAST, -190, "Selected Services");

        this.renderServiceTable(SpringLayout.WEST, 50, serviceTable);
        this.renderServiceTable(SpringLayout.EAST, -50, selectedServicesTable);

        contentPane.repaint();
    }

    private JButton getAddToBasketButton(ArrayList<ActivityService> selectedServices) {
        JButton addToBasketButton = new JButton("Add to basket");
        addToBasketButton.setPreferredSize(new Dimension(150, 40));
        addToBasketButton.addActionListener(e -> {

            Booking booking = new Booking(selectedActivity, itinerary);

            for (ActivityService service : selectedServices) {
                booking.addSelectedService(new BookingActivityService(service, booking));
            }

            itinerary.addBooking(booking);

            int basketTotal = getBasketValueWithoutDiscount();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
            basketButton.setText(currencyFormatter.format(basketTotal / 10));

            this.renderActivityListDiplsay();
        });

        return addToBasketButton;
    }

    private int getBasketValueWithoutDiscount() {
        ArrayList<Booking> bookings = itinerary.getBookings();

        int total = 0;

        for (Booking booking : bookings) {
            total += booking.getTotalCost();
        }

        return total;
    }

    private int sumServicePrice(ArrayList<ActivityService> services) {
        int total = 0;

        for (ActivityService service : services) {
            total += service.getPrice();
        }

        return total;
    }

    private void renderServiceLabel(String horizontalConstraint, int horizontalOffset, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 20));

        contentPane.add(label);
        layout.putConstraint(horizontalConstraint, label, horizontalOffset, horizontalConstraint, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, label, 140, SpringLayout.VERTICAL_CENTER, contentPane);
    }

    private void renderServiceTable(String horizontalConstraint, int horizontalOffset, JTable table) {
        JScrollPane serviceTablePane = new JScrollPane(table);
        serviceTablePane.setBounds(30, 40, 300, 100);
        serviceTablePane.setPreferredSize(new Dimension(300, 100));

        contentPane.add(serviceTablePane);
        layout.putConstraint(horizontalConstraint, serviceTablePane, horizontalOffset, horizontalConstraint, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, serviceTablePane, 200, SpringLayout.VERTICAL_CENTER, contentPane);
    }

    private JTable getServicesTable(ArrayList<ActivityService> services1, ArrayList<ActivityService> services2, DefaultTableModel model1, DefaultTableModel model2, String callToAction, Function<Void, Void> valueAdjuster) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);

        for (ActivityService service : services1) {
            String[] serviceRow = {service.getTitle(), formatter.format(service.getPrice() / 10), "Add"};
            model1.addRow(serviceRow);
        }

        return new JTable(model1) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public void valueChanged(ListSelectionEvent evt) {
                if (!services1.isEmpty() && !evt.getValueIsAdjusting() && this.getSelectedRow() > -1) {
                    ActivityService selectedService = services1.get(this.getSelectedRow());

                    services2.add(selectedService);
                    services1.removeIf(service -> service.getId().equals(selectedService.getId()));

                    model1.removeRow(this.getSelectedRow());
                    model2.addRow(new String[]{selectedService.getTitle(), formatter.format(selectedService.getPrice() / 10), callToAction});
                    valueAdjuster.apply(null);
                    contentPane.revalidate();
                }
                super.valueChanged(evt);
            }
        };
    }

    public void getHeaderButtons(ActionListener backButtonActionListener) {
        File basketImageFile = new File("images/cart.png");

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
