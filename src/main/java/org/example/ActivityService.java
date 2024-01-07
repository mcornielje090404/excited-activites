package org.example;

import java.util.ArrayList;

public class ActivityService extends DatabaseTable<ActivityService> {
    private Activity activity;
    private String title;
    private int price;
    private String serviceCode;
    private ArrayList<BookingActivityService> bookings;
}
