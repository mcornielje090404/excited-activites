package org.example;

import java.awt.print.Book;
import java.util.ArrayList;

public class Activity extends DatabaseTable<Activity> {
    private int baseCost;
    private String title;
    private String description;
    private String location;
    private String activityDate;
    private float duration;
    private boolean insuranceRequired;
    private int insuranceCost;
    private String activityCode;

    public Activity(String id) {
        this.getEntityById("Activity", id);
    }

    public Activity(String[] csvData) {
        this.createObject(csvData);
    }

    @Override
    public Activity createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.baseCost = Integer.parseInt(csvData[1]);
        this.title = csvData[2];
        this.description = csvData[3];
        this.location = csvData[4];
        this.activityDate = csvData[5];
        this.duration = Float.parseFloat(csvData[6]);
        this.insuranceRequired = Boolean.parseBoolean(csvData[7]);
        this.insuranceCost = Integer.parseInt(csvData[8]);
        this.activityCode = csvData[9];

        return this;
    }

    public ArrayList<Booking> getBookings() {
        CSVReader csvReader = new CSVReader();
        ArrayList<Booking> bookings = new ArrayList<>();
        ArrayList<String[]> rawBookings = csvReader.getManyNestedEntitiesById("Booking", this.getId(), "activityId");

        for (String[] bookingData : rawBookings) {
            bookings.add(new Booking(this, bookingData[0]));
        }

        return bookings;
    }

    public ArrayList<ActivityService> getServices() {
        CSVReader csvReader = new CSVReader();
        ArrayList<ActivityService> services = new ArrayList<>();
        ArrayList<String[]> rawServices = csvReader.getManyNestedEntitiesById("ActivityService", this.getId(), "activityId");

        for (String[] serviceData : rawServices) {
            services.add(new ActivityService(this, serviceData[0]));
        }

        return services;
    }

    public int getBaseCost() {
        return this.baseCost;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public String getActivityDate() {
        return this.activityDate;
    }

    public float getDuration() {
        return this.duration;
    }

    public boolean getInsuranceRequired() {
        return this.insuranceRequired;
    }

    public int getInsuranceCost() {
        return this.insuranceCost;
    }

    public String getActivityCode() {
        return this.activityCode;
    }
}
