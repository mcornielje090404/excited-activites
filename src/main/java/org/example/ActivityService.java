package org.example;

import java.util.ArrayList;

public class ActivityService extends DatabaseTable<ActivityService> {
    private Activity activity;
    private String title;
    private int price;
    private String serviceCode;
    private ArrayList<BookingActivityService> bookings;

    public ActivityService(Activity activity, String id) {
        this.getEntityById("ActivityService", id);
        this.activity = activity;
    }

    public ActivityService(String id) {
        this.getEntityById("ActivityService", id);
    }

    public ActivityService createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.title = csvData[2];
        this.price = Integer.parseInt(csvData[3]);
        this.serviceCode = csvData[4];

        if (this.activity == null) {
            this.activity = new Activity(csvData[1]);
        }

        return this;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPrice() {
        return this.price;
    }

    public String getServiceCode() {
        return this.serviceCode;
    }
}
