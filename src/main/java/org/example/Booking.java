package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Booking extends DatabaseTable<Booking> {
    private Activity activity;
    private Itinerary itinerary;
    private boolean insuranceIncluded;
    private ArrayList<BookingActivityService> selectedServices;

    public Booking(String id) {
        this.getEntityById("Booking", id);
        this.fetchActivity(id);
        this.fetchItinerary(id);
    }

    public Booking(Activity activity, String id) {
        this.getEntityById("Booking", id);
        this.activity = activity;
        this.fetchItinerary(id);
    }

    public Booking(Itinerary itinerary, String id) {
        this.getEntityById("Booking", id);
        this.itinerary = itinerary;
        this.fetchActivity(id);
    }

    public Booking createObject(String[] csvData) {
        this.setId(csvData[0]);
        this.insuranceIncluded = Boolean.parseBoolean(csvData[3]);

        return this;
    }

    private void fetchItinerary(String id) {
        CSVReader csvReader = new CSVReader();
        String[] bookingHeaders = csvReader.getHeaders("Booking");
        int itineraryIdIndex = Arrays.stream(bookingHeaders).toList().indexOf("itineraryId");
        String[] bookingData = csvReader.getEntityDataById("Booking", id);

        this.itinerary = new Itinerary(bookingData[itineraryIdIndex]);
    }

    private void fetchActivity(String id) {
        CSVReader csvReader = new CSVReader();
        String[] bookingHeaders = csvReader.getHeaders("Booking");
        int activityIdIndex = Arrays.stream(bookingHeaders).toList().indexOf("activityId");
        String[] bookingData = csvReader.getEntityDataById("Booking", id);

        this.activity = new Activity(bookingData[activityIdIndex]);
    }

    public Activity getActivity() {
        return this.activity;
    }

    public boolean getInsuranceIncluded() {
        return this.insuranceIncluded;
    }

    public Itinerary getItinerary() {
        return this.itinerary;
    }
}
