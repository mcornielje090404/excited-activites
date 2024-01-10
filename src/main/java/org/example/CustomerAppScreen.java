package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
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

    CustomerAppScreen() {}

    public void createPanel() {
        this.renderActivityListDisplay();
    }

    public void renderActivityListDisplay() {
        contentPane.removeAll();
        this.getHeaderButtons(mainMenuButtonActionListener, false);

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

                String[] activityRow = {activity.getTitle(), activity.getLocation(), dateFormatter.format(date), String.valueOf(activity.getDuration()), currencyFormatter.format(activity.getBaseCost() / 100)};
                activityRows.add(activityRow);
            }
        }

        return createActivityTable(activityHeaders, activityRows, filteredActivities);
    }

    private JTable createActivityTable(String[] activityHeaders, ArrayList<String[]> activityRows, ArrayList<Activity> filteredActivities) {
        JTable activityTable = new JTable(activityRows.toArray(new String[0][0]), activityHeaders) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
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

        this.getHeaderButtons(e -> renderActivityListDisplay(), false);

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

        JLabel totalCostLabel = new JLabel("Total Cost: " + NumberFormat.getCurrencyInstance(Locale.UK).format(totalCost / 100));
        totalCostLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        contentPane.add(totalCostLabel);
        layout.putConstraint(SpringLayout.EAST, totalCostLabel, -50, SpringLayout.EAST, contentPane);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, totalCostLabel, 275, SpringLayout.VERTICAL_CENTER, contentPane);

        Function<Void, Void> valueAdjuster = (Void) -> {
            float total = (float) (selectedActivity.getBaseCost() + sumServicePrice(selectedServices)) / 100;
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

            double basketTotal = getDiscountedTotal(itinerary);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
            basketButton.setText(currencyFormatter.format(basketTotal / 100));

            this.renderActivityListDisplay();
        });

        return addToBasketButton;
    }

    private int getBasketValueWithoutDiscount(Itinerary itinerary) {
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
            String[] serviceRow = {service.getTitle(), formatter.format(service.getPrice() / 100), "Add"};
            model1.addRow(serviceRow);
        }

        return new JTable(model1) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!services1.isEmpty() && !evt.getValueIsAdjusting() && this.getSelectedRow() > -1) {
                    ActivityService selectedService = services1.get(this.getSelectedRow());

                    services2.add(selectedService);
                    services1.removeIf(service -> service.getId().equals(selectedService.getId()));

                    model1.removeRow(this.getSelectedRow());
                    model2.addRow(new String[]{selectedService.getTitle(), formatter.format(selectedService.getPrice() / 100), callToAction});
                    valueAdjuster.apply(null);
                    contentPane.revalidate();
                }
                super.valueChanged(evt);
            }
        };
    }

    private void renderBasketDisplay() {
        contentPane.removeAll();

        this.getHeaderButtons(e -> renderActivityListDisplay(), true);
        boolean showBasket = !itinerary.getBookings().isEmpty();

        if (showBasket) {
            ArrayList<Booking> bookings = itinerary.getBookings();

            double totalCost = getDiscountedTotal(itinerary);

            JButton buyButton = new JButton("Buy now");
            buyButton.setPreferredSize(new Dimension(150, 40));
            buyButton.setVisible(false);

            contentPane.add(buyButton);

            layout.putConstraint(SpringLayout.EAST, buyButton, -150, SpringLayout.EAST, contentPane);
            layout.putConstraint(SpringLayout.SOUTH, buyButton, -100, SpringLayout.SOUTH, contentPane);

            buyButton.addActionListener(e -> {
                saveItinerary();
                JOptionPane.showMessageDialog(null, "Thank you for your purchase!");
                itinerary = new Itinerary();
                basketButton.setText("£0.00");
                renderActivityListDisplay();
            });

            JLabel totalCostLabel = new JLabel("Total Cost: " + NumberFormat.getCurrencyInstance(Locale.UK).format(totalCost / 100));
            totalCostLabel.setFont(new Font("Arial", Font.PLAIN, 20));

            contentPane.add(totalCostLabel);

            layout.putConstraint(SpringLayout.WEST, totalCostLabel, 150, SpringLayout.WEST, contentPane);
            layout.putConstraint(SpringLayout.SOUTH, totalCostLabel, -105, SpringLayout.SOUTH, contentPane);

            String[] addOnsTableHeaders = {"Itinerary Add-ons"};
            DefaultTableModel addOnsTableModel = new DefaultTableModel(addOnsTableHeaders, 0);


            for (Booking booking : bookings) {
                for (BookingActivityService bookingActivityService : booking.getSelectedServices()) {
                    ActivityService activityService = bookingActivityService.getActivityService();
                    addOnsTableModel.addRow(new String[]{activityService.getTitle()});
                }
            }

            JTable addOnsTable = new JTable(addOnsTableModel);
            JScrollPane addOnsTablePane = new JScrollPane(addOnsTable);
            addOnsTablePane.setPreferredSize(new Dimension(500, 100));

            layout.putConstraint(SpringLayout.WEST, addOnsTablePane, 150, SpringLayout.WEST, contentPane);
            layout.putConstraint(SpringLayout.VERTICAL_CENTER, addOnsTablePane, 0, SpringLayout.VERTICAL_CENTER, contentPane);

            contentPane.add(addOnsTablePane);

            String[] activityTableHeaders = {"Activity", "Add-ons"};
            DefaultTableModel activityTableModel = new DefaultTableModel(activityTableHeaders, 0);

            for (Booking booking : bookings) {
                Activity activity = booking.getActivity();
                StringBuilder addOns = new StringBuilder();

                for (BookingActivityService bookingActivityService : booking.getSelectedServices()) {
                    ActivityService activityService = bookingActivityService.getActivityService();
                    addOns.append(activityService.getTitle()).append(", ");
                }

                activityTableModel.addRow(new String[]{activity.getTitle(), addOns.length() > 2 ? addOns.substring(0, addOns.length() - 2) : addOns.toString()});
            }

            JTable activitiesTable = new JTable(activityTableModel);
            JScrollPane activitiesTablePane = new JScrollPane(activitiesTable);
            activitiesTablePane.setPreferredSize(new Dimension(500, 100));

            layout.putConstraint(SpringLayout.WEST, activitiesTablePane, 150, SpringLayout.WEST, contentPane);
            layout.putConstraint(SpringLayout.VERTICAL_CENTER, activitiesTablePane, 150, SpringLayout.VERTICAL_CENTER, contentPane);

            contentPane.add(activitiesTablePane);

            this.renderLeadAttendeeSection(buyButton, totalCostLabel);
        } else {
            JLabel emptyBasketLabel = new JLabel("Your basket is empty");
            emptyBasketLabel.setFont(new Font("Arial", Font.PLAIN, 20));

            contentPane.add(emptyBasketLabel);

            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, emptyBasketLabel, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
            layout.putConstraint(SpringLayout.VERTICAL_CENTER, emptyBasketLabel, 0, SpringLayout.VERTICAL_CENTER, contentPane);
        }

        contentPane.revalidate();
        contentPane.repaint();
    }

    public double getDiscountedTotal(Itinerary itinerary) {
        int total = getBasketValueWithoutDiscount(itinerary);

        ArrayList<Booking> bookings = itinerary.getBookings();

        int numBookings = bookings.size();
        int numAttendees = itinerary.getNumOfAttendees();

        if (numBookings > 0) {
            if (numBookings <= 2) {
                if (numAttendees >= 20) {
                    total -= total * 0.08;
                } else if (numAttendees >= 10) {
                    total -= total * 0.05;
                }
            } else if (numBookings <= 5) {
                if (numAttendees >= 20) {
                    total -= total * 0.12;
                } else if (numAttendees >= 10) {
                    total -= total * 0.08;
                } else {
                    total -= total * 0.05;
                }
            } else {
                if (numAttendees >= 20) {
                    total -= total * 0.14;
                } else if (numAttendees >= 10) {
                    total -= total * 0.12;
                } else {
                    total -= total * 0.1;
                }
            }
        }

        return total;
    }

    public void renderLeadAttendeeSection(JButton buyButton, JLabel totalCostLabel) {
        boolean editScreen = itinerary.getLeadAttendee() == null;

        JLabel leadAttendeeSectionLabel = new JLabel("Lead Attendee Details");
        leadAttendeeSectionLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton submitAttendeeButton = new JButton("Submit");
        submitAttendeeButton.setPreferredSize(new Dimension(150, 40));

        JButton editAttendeeButton = new JButton("Edit");
        editAttendeeButton.setPreferredSize(new Dimension(150, 40));

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, submitAttendeeButton, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, submitAttendeeButton, 260, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.WEST, leadAttendeeSectionLabel, 150, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, leadAttendeeSectionLabel, 80, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.WEST, editAttendeeButton, 145, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.NORTH, editAttendeeButton, 190, SpringLayout.NORTH, contentPane);

        contentPane.add(leadAttendeeSectionLabel);

        JLabel firstNameLabel = new JLabel("First name");
        JTextField firstNameTextField = new JTextField();
        JLabel firstNameErrorLabel = new JLabel("Must provide a value for first name");
        firstNameErrorLabel.setVisible(false);
        firstNameErrorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        firstNameTextField.setPreferredSize(new Dimension(200, 30));
        firstNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!(Character.isLetter(evt.getKeyChar()))) {
                    evt.consume();
                }
            }
        });

        JLabel lastNameLabel = new JLabel("Last name");
        JTextField lastNameTextField = new JTextField();
        JLabel lastNameErrorLabel = new JLabel("Must provide a value for last name");
        lastNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        lastNameTextField.setPreferredSize(new Dimension(200, 30));
        lastNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!(Character.isLetter(evt.getKeyChar()))) {
                    evt.consume();
                }
            }
        });

        contentPane.add(firstNameErrorLabel);
        contentPane.add(lastNameErrorLabel);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, firstNameLabel, -215, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, firstNameLabel, 120, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, firstNameErrorLabel, -165, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, firstNameErrorLabel, 162, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, firstNameTextField, -150, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, firstNameTextField, 135, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lastNameLabel, 85, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, lastNameLabel, 120, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lastNameErrorLabel, 135, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, lastNameErrorLabel, 162, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, lastNameTextField, 150, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, lastNameTextField, 135, SpringLayout.NORTH, contentPane);

        AtomicInteger numAttendees = new AtomicInteger(1);

        JButton addAttendeeButton = new JButton("+");
        JButton removeAttendeeButton = new JButton("-");
        JLabel numAttendeesTitle = new JLabel("Number of attendees");
        JLabel numAttendeesLabel = new JLabel(String.valueOf(numAttendees.get()));

        addAttendeeButton.setPreferredSize(new Dimension(50, 50));
        removeAttendeeButton.setPreferredSize(new Dimension(50, 50));

        addAttendeeButton.addActionListener(e -> {
            numAttendees.getAndIncrement();
            numAttendeesLabel.setText(String.valueOf(numAttendees.get()));
        });

        removeAttendeeButton.addActionListener(e -> {
            if (numAttendees.get() > 1) {
                numAttendees.getAndDecrement();
                numAttendeesLabel.setText(String.valueOf(numAttendees.get()));
            }
        });

        JLabel nameDisplayLabel = new JLabel("Name: " + (editScreen ? "N/A" : itinerary.getLeadAttendee().getFirstName().charAt(0) + " " + itinerary.getLeadAttendee().getLastName()));
        JLabel numAttendeesDisplayLabel = new JLabel("Number of attendees: " + (editScreen ? "N/A" : itinerary.getNumOfAttendees()));

        nameDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        numAttendeesDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        layout.putConstraint(SpringLayout.WEST, nameDisplayLabel, 150, SpringLayout.WEST, contentPane);
        layout.putConstraint(SpringLayout.WEST, numAttendeesDisplayLabel, 150, SpringLayout.WEST, contentPane);

        layout.putConstraint(SpringLayout.NORTH, nameDisplayLabel, 120, SpringLayout.NORTH, contentPane);
        layout.putConstraint(SpringLayout.NORTH, numAttendeesDisplayLabel, 140, SpringLayout.NORTH, contentPane);

        submitAttendeeButton.addActionListener(e -> {
            firstNameErrorLabel.setVisible(false);
            lastNameErrorLabel.setVisible(false);

            if (!firstNameTextField.getText().isEmpty() && !lastNameTextField.getText().isEmpty()) {
                LeadAttendee leadAttendee = new LeadAttendee(itinerary, firstNameTextField.getText(), lastNameTextField.getText());

                itinerary.setNumOfAttendees(numAttendees.get());
                itinerary.setLeadAttendee(leadAttendee);

                submitAttendeeButton.setVisible(false);
                firstNameLabel.setVisible(false);
                firstNameTextField.setVisible(false);
                lastNameLabel.setVisible(false);
                lastNameTextField.setVisible(false);
                addAttendeeButton.setVisible(false);
                removeAttendeeButton.setVisible(false);
                numAttendeesLabel.setVisible(false);
                numAttendeesTitle.setVisible(false);

                nameDisplayLabel.setText("Name: " + itinerary.getLeadAttendee().getFirstName().charAt(0) + " " + itinerary.getLeadAttendee().getLastName());
                numAttendeesDisplayLabel.setText("Number of attendees: " + itinerary.getNumOfAttendees());

                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK);

                totalCostLabel.setText("Total Cost: " + currencyFormatter.format(getDiscountedTotal(itinerary) / 100));
                basketButton.setText(currencyFormatter.format(getDiscountedTotal(itinerary) / 100));

                nameDisplayLabel.setVisible(true);
                numAttendeesDisplayLabel.setVisible(true);
                editAttendeeButton.setVisible(true);
                buyButton.setVisible(true);

                contentPane.revalidate();
                contentPane.repaint();
            } else {
                if (firstNameTextField.getText().isEmpty()) {
                    firstNameErrorLabel.setVisible(true);
                }

                if (lastNameTextField.getText().isEmpty()) {
                    lastNameErrorLabel.setVisible(true);
                }
            }
        });

        editAttendeeButton.addActionListener(e -> {
            submitAttendeeButton.setVisible(true);
            firstNameLabel.setVisible(true);
            firstNameTextField.setVisible(true);
            lastNameLabel.setVisible(true);
            lastNameTextField.setVisible(true);
            addAttendeeButton.setVisible(true);
            removeAttendeeButton.setVisible(true);
            numAttendeesLabel.setVisible(true);
            numAttendeesTitle.setVisible(true);
            nameDisplayLabel.setVisible(false);
            numAttendeesDisplayLabel.setVisible(false);
            editAttendeeButton.setVisible(false);
            buyButton.setVisible(false);

            contentPane.revalidate();
            contentPane.repaint();
        });

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, addAttendeeButton, 225, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, addAttendeeButton, 205, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, removeAttendeeButton, -225, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, removeAttendeeButton, 205, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, numAttendeesLabel, 0, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, numAttendeesLabel, 225, SpringLayout.NORTH, contentPane);

        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, numAttendeesTitle, -180, SpringLayout.HORIZONTAL_CENTER, contentPane);
        layout.putConstraint(SpringLayout.NORTH, numAttendeesTitle, 185, SpringLayout.NORTH, contentPane);

        contentPane.add(submitAttendeeButton);
        contentPane.add(firstNameLabel);
        contentPane.add(firstNameTextField);
        contentPane.add(lastNameLabel);
        contentPane.add(lastNameTextField);
        contentPane.add(addAttendeeButton);
        contentPane.add(removeAttendeeButton);
        contentPane.add(numAttendeesLabel);
        contentPane.add(numAttendeesTitle);
        contentPane.add(nameDisplayLabel);
        contentPane.add(numAttendeesDisplayLabel);
        contentPane.add(editAttendeeButton);

        if (editScreen) {
            submitAttendeeButton.setVisible(true);
            firstNameLabel.setVisible(true);
            firstNameTextField.setVisible(true);
            lastNameLabel.setVisible(true);
            lastNameTextField.setVisible(true);
            addAttendeeButton.setVisible(true);
            removeAttendeeButton.setVisible(true);
            numAttendeesLabel.setVisible(true);
            numAttendeesTitle.setVisible(true);

            nameDisplayLabel.setVisible(false);
            numAttendeesDisplayLabel.setVisible(false);
            editAttendeeButton.setVisible(false);
        } else {
            submitAttendeeButton.setVisible(false);
            firstNameLabel.setVisible(false);
            firstNameTextField.setVisible(false);
            lastNameLabel.setVisible(false);
            lastNameTextField.setVisible(false);
            addAttendeeButton.setVisible(false);
            removeAttendeeButton.setVisible(false);
            numAttendeesLabel.setVisible(false);
            numAttendeesTitle.setVisible(false);

            nameDisplayLabel.setVisible(true);
            numAttendeesDisplayLabel.setVisible(true);
            editAttendeeButton.setVisible(true);
            buyButton.setVisible(true);
        }
    }

    public void getHeaderButtons(ActionListener backButtonActionListener, boolean isBasketScreen) {
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

        if (backIconFile.exists()) {
            try {
                Image backImage = ImageIO.read(backIconFile).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                backButton.setIcon(new ImageIcon(backImage));
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

        basketButton.addActionListener(e -> renderBasketDisplay());
        backButton.addActionListener(backButtonActionListener);

        if (!isBasketScreen) {
            basketButton.setPreferredSize(new Dimension(150, 75));
            contentPane.add(basketButton);
            layout.putConstraint(SpringLayout.EAST, basketButton, 0, SpringLayout.EAST, contentPane);
        }

        backButton.setPreferredSize(new Dimension(150, 75));
        contentPane.add(backButton);
        layout.putConstraint(SpringLayout.WEST, backButton, 0, SpringLayout.WEST, contentPane);
    }

    private void saveItinerary() {
        CSVWriter writer = new CSVWriter();

        StringBuilder itineraryReference = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            if (i == 3) {
                itineraryReference.append("-");
            } else {
                itineraryReference.append(getRandomUppercaseCharacter());
            }
        }

        itinerary.setItineraryReference(itineraryReference.toString());

        ArrayList<Booking> bookings = itinerary.getBookings();

        ArrayList<String> rawBookings = new ArrayList<>();
        ArrayList<String> rawBookingActivityServices = new ArrayList<>();

        for (Booking booking : bookings) {
            for (BookingActivityService bookingActivityService : booking.getSelectedServices()) {
                String rawBookingActivityString = bookingActivityService.getId() + "," +
                        booking.getId() + "," +
                        bookingActivityService.getActivityService().getId();

                rawBookingActivityServices.add(rawBookingActivityString);
            }

            String rawBookingString = booking.getId() + "," +
                    booking.getActivity().getId() + "," +
                    booking.getItinerary().getId() + "," +
                    "false";

            rawBookings.add(rawBookingString);
        }

        String rawLeadAttendeeString = itinerary.getLeadAttendee().getId() + "," +
                itinerary.getLeadAttendee().getFirstName() + "," +
                itinerary.getLeadAttendee().getLastName() + "," +
                itinerary.getId();

        String rawItineraryString = itinerary.getId() + "," +
                itinerary.getNumOfAttendees() + "," +
                itinerary.getItineraryReference() + "," +
                itinerary.getLeadAttendee().getId();

        writer.writeMultipleLinesToTable(rawBookings, "Booking");
        writer.writeMultipleLinesToTable(rawBookingActivityServices, "BookingActivityService");
        writer.writeLineToTable(rawLeadAttendeeString, "LeadAttendee");
        writer.writeLineToTable(rawItineraryString, "Itinerary");
    }

    private Character getRandomUppercaseCharacter() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        return Character.toUpperCase(alphabet.charAt(random.nextInt(alphabet.length())));
    }
}
