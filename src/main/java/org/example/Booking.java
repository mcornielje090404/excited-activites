package org.example;

import java.util.ArrayList;

public class Booking extends DatabaseTable<Booking> {
    private Activity activity;
    private Itinerary itinerary;
    private boolean insuranceIncluded;
    private ArrayList<BookingActivityService> selectedServices;
}
