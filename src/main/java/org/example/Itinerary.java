package org.example;

import java.util.ArrayList;

public class Itinerary extends DatabaseTable<Itinerary> {
    private ArrayList<Booking> bookings;
    private int numOfAttendees;
    private String itineraryReference;
    private LeadAttendee leadAttendee;
}
