package org.example;

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
    private ArrayList<ActivityService> services;
    private ArrayList<Booking> bookings;
}
