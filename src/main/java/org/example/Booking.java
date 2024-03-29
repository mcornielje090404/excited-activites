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

    public Booking(Itinerary itinerary, String id) {
        this.getEntityById("Booking", id);
        this.itinerary = itinerary;
        this.fetchActivity(id);
        this.fetchSelectedServices(id);
    }

    public Booking(Activity activity, Itinerary itinerary) {
        this.setId(this.dbClient.getUniqueUUID());
        this.activity = activity;
        this.itinerary = itinerary;
        this.selectedServices = new ArrayList<>();
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

    private void fetchSelectedServices(String id) {
        CSVReader csvReader = new CSVReader();
        ArrayList<BookingActivityService> selectedServices = new ArrayList<>();
        ArrayList<String[]> rawBookingActivityServices = csvReader.getManyNestedEntitiesById("BookingActivityService", id, "bookingId");


        for (String[] bookingActivityServiceData : rawBookingActivityServices) {
            BookingActivityService bookingActivityService = new BookingActivityService(this, bookingActivityServiceData);
            selectedServices.add(bookingActivityService);
        }

        this.selectedServices = selectedServices;
    }

    public ArrayList<BookingActivityService> getSelectedServices() {
        return this.selectedServices;
    }

    public void addSelectedService(BookingActivityService bookingActivityService) {
        this.selectedServices.add(bookingActivityService);
    }

    public int getTotalCost() {
        int totalCost = this.activity.getBaseCost();

        if (this.insuranceIncluded) {
            totalCost += this.activity.getInsuranceCost();
        }

        for (BookingActivityService bookingActivityService : this.selectedServices) {
            totalCost += bookingActivityService.getActivityService().getPrice();
        }

        return totalCost;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public Itinerary getItinerary() {
        return this.itinerary;
    }
}
